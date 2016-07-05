package org.sovaj.plugin.mongo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.util.StringUtils;

/**
 * Goal which create a mongodb
 *
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = false)
public class CreateMongoDBMicroService extends AbstractMojo {

    @Component
    private Prompter prompter;

    @Parameter(defaultValue = "${plugin}", readonly = true) // Maven 3 only
    private PluginDescriptor plugin;

    @Parameter(property = "sayhi.greeting", defaultValue = "Hello World!")
    private String greeting;

    public void execute()
            throws MojoExecutionException {
        try {

            byte[] buffer = new byte[1024];

            String projectGroupId = prompter.prompt("Please enter your project groupId : ");
            String projectName = prompter.prompt("Please enter your project name : ");
            String projectVersion = prompter.prompt("Please enter your project version : 1.0.0.0-SNAPSHOT ");
            if (StringUtils.isEmpty(projectVersion)) {
                projectVersion = "1.0.0.0-SNAPSHOT";
            }
            String projectTrigram = prompter.prompt("Please enter your project trigram: ");
            File f = new File(projectName);

            if (!f.exists()) {
                f.mkdirs();
            }

            String outputFolder = f.getAbsolutePath();

            File artifact = plugin.getPluginArtifact().getFile();
            getLog().info(artifact.getAbsolutePath());

            ZipInputStream zis = new ZipInputStream(artifact.toURI().toURL().openStream());
            /*  create a context and add data */
            VelocityContext context = new VelocityContext();
            context.put("groupId", projectGroupId);
            context.put("artifactId", projectVersion);
            context.put("projectTrigram", projectTrigram);
            context.put("version", projectVersion);

            /*  first, get and initialize an engine  */
            VelocityEngine ve = new VelocityEngine();
            ve.init();
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

                    File outputFile = new File(outputFolder + File.separator + fileName.substring(12));
                    new File(outputFile.getParent()).mkdirs();
                    getLog().info(outputFile.getAbsolutePath());
                    try (FileWriter outputFileWriter = new FileWriter(outputFile)) {
                        ve.evaluate(context, outputFileWriter, "log", inputFileInputStream);
                        outputFileWriter.flush();
                    }
                }
            }
            File templateFolder = new File(outputFolder + "/app-template");
            getLog().debug("### Try to delete " + templateFolder.getAbsolutePath());
            FileUtils.deleteDirectory(templateFolder);
//            FileWriter w = null;
//            try {
//                w = new FileWriter(touch);
//
//                w.write(String.format("<groupId>%1s<groupId>\n", groupId));
//                w.write(String.format("<artifactId>%1s-parent<artifactId>\n", projectTrigram));
//                w.write(String.format("<version>%1s<projectVersion>\n", projectVersion));
//            } catch (IOException e) {
//                throw new MojoExecutionException("Error creating file " + touch, e);
//            } finally {
//                if (w != null) {
//                    try {
//                        w.close();
//                    } catch (IOException e) {
//                        // ignore
//                    }
//                }
//            }
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

}
