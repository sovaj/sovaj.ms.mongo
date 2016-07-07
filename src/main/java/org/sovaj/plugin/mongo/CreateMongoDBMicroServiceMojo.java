package org.sovaj.plugin.mongo;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;
import org.xml.sax.InputSource;

/**
 * Goal which create a mongodb rest api project
 *
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = false)
public class CreateMongoDBMicroServiceMojo extends AbstractMojo {

    @Component
    private Prompter prompter;

    @Parameter(defaultValue = "${plugin}", readonly = true) // Maven 3 only
    private PluginDescriptor plugin;

    @Parameter(property = "groupId")
    private String projectGroupId = null;

    @Parameter(property = "name")
    private String projectName = null;

    @Parameter(property = "trigram")
    private String projectTrigram = null;

    @Parameter(property = "xsd")
    private String xsdFileLocation = null;

    private String validate = null;
    private File xsdFile = null;
    private String xsdFileName = null;
    private String projectVersion = null;
    private String projectPackage = null;

    @Override
    public void execute()
            throws MojoExecutionException {
        try {

            retrieveProjectInformation();
            String outputFolder = buildBaseProject();
            copyXSD(outputFolder);
            buildDomainObject(outputFolder);
            writeSOVAJFile(outputFolder);
            deleteTemplateFolder(outputFolder);

        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private String buildBaseProject() throws ResourceNotFoundException, Exception, ParseErrorException, MethodInvocationException, IOException {
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("groupId", projectGroupId);
        context.put("projectTrigram", projectTrigram);
        context.put("version", projectVersion);
        context.put("xsdFileName", xsdFileName);
        context.put("package", projectPackage);
        /*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        byte[] buffer = new byte[1024];
        File f = new File(projectName);
        if (!f.exists()) {
            f.mkdirs();
        } else {
            throw new Exception(String.format("Project %1s already exist in the current folder", projectName));
        }
        String outputFolder = f.getAbsolutePath();
        File artifact = plugin.getPluginArtifact().getFile();
        getLog().info(artifact.getAbsolutePath());
        ZipInputStream zis = new ZipInputStream(artifact.toURI().toURL().openStream());
        for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry()) {
            String fileName = e.getName();
            if (!e.isDirectory() && fileName.startsWith("app-template")) {

                File inputFile = new File(outputFolder + File.separator + fileName);
                new File(inputFile.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(inputFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                FileInputStream inputFileInputStream = new FileInputStream(inputFile);
                String finalFileName = fileName.substring(12).replaceAll("app-", projectTrigram + "-");
                File outputFile = new File(outputFolder + File.separator + finalFileName);
                new File(outputFile.getParent()).mkdirs();
                getLog().info(outputFile.getAbsolutePath());
                try (FileWriter outputFileWriter = new FileWriter(outputFile)) {
                    ve.evaluate(context, outputFileWriter, "log", inputFileInputStream);
                    outputFileWriter.flush();
                }
            }
        }
        return outputFolder;
    }

    private void deleteTemplateFolder(String outputFolder) throws IOException {
        File templateFolder = new File(outputFolder + "/app-template");
        FileUtils.deleteDirectory(templateFolder);
    }

    private void retrieveProjectInformation() throws PrompterException {
        while (!"Y".equals(validate)) {
            while (StringUtils.isEmpty(projectGroupId)) {
                projectGroupId = prompter.prompt("Define value for property 'groupId': ");
            }
            while (StringUtils.isEmpty(projectName)) {
                projectName = prompter.prompt("Define value for property 'name': ");
            }
            projectVersion = "1.0.0.0-SNAPSHOT";
            while (StringUtils.isEmpty(projectTrigram)) {
                projectTrigram = prompter.prompt("Define value for property 'trigram': ");
            }
            while (StringUtils.isEmpty(xsdFileLocation)) {
                xsdFileLocation = prompter.prompt("Define value for property 'xsd': ");
                xsdFile = new File(xsdFileLocation);
                if (!xsdFile.exists()) {
                    prompter.showMessage("XSD not found please");
                    xsdFileLocation = null;
                }
            }
            xsdFile = new File(xsdFileLocation);
            xsdFileName = xsdFile.getName();
            projectPackage = String.format("%1s.%2s", projectGroupId, projectTrigram);
            System.out.println("groupId:" + projectGroupId);
            System.out.println("name:" + projectName);
            System.out.println("version:" + projectVersion);
            System.out.println("trigram:" + projectTrigram);
            System.out.println("xsd:" + xsdFileLocation);
            validate = prompter.prompt("Y: ");
        }
    }

    private void copyXSD(String outputDir) throws IOException {
        FileUtils.copyFileToDirectory(xsdFile, new File(outputDir + "/"
                + projectTrigram
                + "-domain/src/main/resources/schemas/xsd/"));
    }

    private void buildDomainObject(String outputDir) throws IOException {
        // Configure sources & output
        String schemaPath = xsdFile.getAbsolutePath();
        String outputDirectory = outputDir + "/"
                + projectTrigram
                + "-domain/target/generated-sources/cxf";

        File generatedFiles = new File(outputDirectory);
        generatedFiles.mkdirs();
        // Setup schema compiler
        SchemaCompiler sc = XJC.createSchemaCompiler();
        sc.forcePackageName("com.xyz.schema.generated");

        // Setup SAX InputSource
        File schemaFile = new File(schemaPath);
        InputSource is = new InputSource(new FileInputStream(schemaFile));
        is.setSystemId(schemaFile.toURI().toString());

        // Parse & build
        sc.parseSchema(is);
        S2JJAXBModel model = sc.bind();
        JCodeModel jCodeModel = model.generateCode(null, null);
        jCodeModel.build(new File(outputDirectory));
    }

    private void writeSOVAJFile(String outputFolder) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outputFolder + "/sovaj.properties")) {
            fileWriter.write(String.format("groupId=%1s\n", projectGroupId));
            fileWriter.write(String.format("artifactId=%1s\n", projectVersion));
            fileWriter.write(String.format("projectTrigram=%1s\n", projectTrigram));
            fileWriter.write(String.format("version=%1s\n", projectVersion));
            fileWriter.write(String.format("xsdFileName=%1s\n", xsdFileName));
            fileWriter.write(String.format("package=%1s\n", projectPackage));
            fileWriter.flush();
        }
    }

}
