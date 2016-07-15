package org.sovaj.plugin.mongo;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private String projectGroupId;

    @Parameter(property = "name")
    private String projectName;

    @Parameter(property = "trigram")
    private String projectTrigram;

    @Parameter(property = "xsd")
    private String xsdFileLocation;

    private String validate;
    private File xsdFile;
    private String xsdFileName;
    private String projectVersion;
    private String projectPackage;
    private String resourceToManage;

    @Override
    public void execute()
            throws MojoExecutionException {
        try {

            retrieveProjectInformation();
            String outputFolder = createProjectFolder();
            copyContract(outputFolder);
            buildDomainObject(outputFolder);
            buildBaseProject(outputFolder);
            writeSOVAJFile(outputFolder);
            deleteTemplateFolder(outputFolder);

        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private String buildBaseProject(String outputFolder) throws ResourceNotFoundException, Exception, ParseErrorException, MethodInvocationException, IOException {
        /*  create a context and add data */
        VelocityContext context = createContext();

        /*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        byte[] buffer = new byte[1024];

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
                String finalFileName = fileName.substring(12).replaceAll("app-", projectTrigram + "-")
                        .replace("projectTrigram", getFolderFromPackage())
                        .replace("resourceToManage", resourceToManage);
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

    private VelocityContext createContext() {
        VelocityContext context = new VelocityContext();
        context.put("groupId", projectGroupId);
        context.put("projectTrigram", projectTrigram);
        context.put("version", projectVersion);
        context.put("xsdFileName", xsdFileName);
        context.put("package", projectPackage);
        context.put("name", projectName);
        char c[] = resourceToManage.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        resourceToManage = new String(c);
        context.put("resourceToManage", resourceToManage);
        c = resourceToManage.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String _resourceToManage = new String(c);
        context.put("_resourceToManage", _resourceToManage);
        return context;
    }

    private void deleteTemplateFolder(String outputFolder) throws IOException {
        File templateFolder = new File(outputFolder + "/app-template");
        FileUtils.deleteDirectory(templateFolder);
    }

    private void retrieveProjectInformation() throws PrompterException {
        while (!"Y".equals(validate)) {
            while (StringUtils.isEmpty(projectGroupId)) {
                projectGroupId = prompter.prompt("Define value for property 'groupId'");
            }
            while (StringUtils.isEmpty(projectName)) {
                projectName = prompter.prompt("Define value for property 'name'");
            }
            while (StringUtils.isEmpty(projectVersion)) {
                projectVersion = prompter.prompt("Define value for property 'version' : ", "1.0.0.0-SNAPSHOT");
            }
            while (StringUtils.isEmpty(projectTrigram)) {
                projectTrigram = prompter.prompt("Define value for property 'trigram'");
            }
            while (StringUtils.isEmpty(xsdFileLocation)) {
                xsdFileLocation = prompter.prompt("Define value for property 'xsd'");
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
            validate = prompter.prompt("Is thie the configuration for your project", Arrays.asList("Y", "N"));
        }
    }

    private void copyContract(String outputDir) throws IOException {
        File xsdFolder = new File(outputDir + "/"
                + projectTrigram
                + "-domain/src/main/resources/schemas/xsd");
        xsdFolder.mkdirs();
        File jsonSchemaFolder = new File(outputDir + "/"
                + projectTrigram
                + "-domain/src/main/resources/schemas/jsonschema");
        jsonSchemaFolder.mkdirs();
        FileUtils.copyFileToDirectory(xsdFile, xsdFolder);
    }

    private void buildDomainObject(String outputDir) throws IOException, PrompterException {
        resourceToManage = new XSDToDomainClassBuilder(xsdFile, projectGroupId, projectTrigram, prompter, projectPackage).buildDomainObject(outputDir);
    }

    private void writeSOVAJFile(String outputFolder) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outputFolder + "/sovaj.properties")) {
            fileWriter.write(String.format("groupId=%1s\n", projectGroupId));
            fileWriter.write(String.format("artifactId=%1s\n", projectVersion));
            fileWriter.write(String.format("projectTrigram=%1s\n", projectTrigram));
            fileWriter.write(String.format("version=%1s\n", projectVersion));
            fileWriter.write(String.format("xsdFileName=%1s\n", xsdFileName));
            fileWriter.write(String.format("package=%1s\n", projectPackage));
            fileWriter.write(String.format("resourceToManage=%1s\n", resourceToManage));
            fileWriter.flush();
        }
    }

    private String getFolderFromPackage() {
        return getFolderFromPackage(projectPackage);
    }

    private String getFolderFromPackage(final String packageName) {
        return packageName.replaceAll("\\.", "/");
    }

    private String createProjectFolder() throws Exception {
        File f = new File(projectName);
        if (!f.exists()) {
            f.mkdirs();
        } else {
            throw new Exception(String.format("Project %1s already exist in the current folder", projectName));
        }
        return f.getAbsolutePath();
    }

}
