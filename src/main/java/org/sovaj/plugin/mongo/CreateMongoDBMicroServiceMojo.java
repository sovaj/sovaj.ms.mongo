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
//            copyXSD(outputFolder);
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

//    private void copyXSD(String outputDir) throws IOException {
//        FileUtils.copyFileToDirectory(xsdFile, new File(outputDir + "/"
//                + projectTrigram
//                + "-domain/src/main/resources/schemas/xsd/"));
//    }

    private void buildDomainObject(String outputDir) throws IOException, PrompterException {
        // Configure sources & output
        String schemaPath = xsdFile.getAbsolutePath();
        String outputDirectory = outputDir + "/app-template/"
                + "app-domain/target/generated-sources/cxf";

        File generatedFiles = new File(outputDirectory);
        generatedFiles.mkdirs();
        // Setup schema compiler
        SchemaCompiler sc = XJC.createSchemaCompiler();
        final String generatedFilePakage = projectGroupId + "." + projectTrigram + ".jaxb";
        final String domainFilePakage = projectGroupId + "." + projectTrigram + ".domain";
        sc.forcePackageName(generatedFilePakage);

        // Setup SAX InputSource
        File schemaFile = new File(schemaPath);
        InputSource is = new InputSource(new FileInputStream(schemaFile));
        is.setSystemId(schemaFile.toURI().toString());

        // Parse & build
        sc.parseSchema(is);
        S2JJAXBModel model = sc.bind();
        JCodeModel jCodeModel = model.generateCode(null, null);
        jCodeModel.build(new File(outputDirectory));

        for (File generatedFile : new File(outputDirectory + "/" + getFolderFromPackage(generatedFilePakage)).listFiles()) {
            String fileName = generatedFile.getName();
            if (!fileName.equals("ObjectFactory.java")
                    && !fileName.equals("package-info.java")) {
                File domainObjectFile = new File(outputDir + "/"
                        + projectTrigram
                        + "-domain/src/main/java/" + getFolderFromPackage() + "/domain/" + fileName);
                domainObjectFile.getParentFile().mkdirs();
                FileWriter domainObjectFileWriter = new FileWriter(domainObjectFile);
                try (BufferedReader br = new BufferedReader(new FileReader(generatedFile))) {
                    boolean onClassDefintionLine = false;
                    boolean businessObjectAdded = false;
                    boolean extendsClass = false;
                    boolean itIsAClass = false;
                    boolean passedClassDefinitionLine = false;
                    for (String line; (line = br.readLine()) != null;) {
                        if (line.trim().startsWith("import javax.xml.bind.annotation")) {
                            continue;
                        }
                        if (!onClassDefintionLine) {
                            onClassDefintionLine = line.trim().startsWith("public");
                            itIsAClass = line.contains("class");
                            if (onClassDefintionLine) {
                                domainObjectFileWriter.write("\n");
                            }
                        }
                        if (onClassDefintionLine) {
                            extendsClass = extendsClass || line.contains("extends");
                            if (line.contains("{")) {
                                if (itIsAClass && !extendsClass) {
                                    line = line.replace("{", "extends BusinessObject {");
                                    businessObjectAdded = true;
                                }
                                onClassDefintionLine = false;
                                passedClassDefinitionLine = true;
                            }
                        }
                        if (!passedClassDefinitionLine) {
                            if (!line.trim().startsWith("import")
                                    && !line.trim().startsWith("package")
                                    && !onClassDefintionLine) {
                                continue;
                            }
                        }

                        if (line.trim().startsWith("@")) {
                            continue;
                        }
                        line = line.replace(generatedFilePakage, domainFilePakage);
                        domainObjectFileWriter.write(line);
                        domainObjectFileWriter.write("\n");
                    }
                    domainObjectFileWriter.flush();
                }
            }
        }

        // String to be scanned to find the pattern.
        String line = new String(Files.readAllBytes(xsdFile.toPath()));
        String pattern = "<.*:element name=\".*\" type=\"(.*)\"/>";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        List<String> possibleResources = new ArrayList();
        while (m.find()) {
            possibleResources.add(m.group(1));
        }
        resourceToManage = prompter.prompt("Please select the resource to manage", possibleResources);

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
