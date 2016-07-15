package org.sovaj.plugin.mongo;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.xml.sax.InputSource;

/**
 *
 * @author Mickael Dubois
 */
public class XSDToDomainClassBuilder {

    private final File xsdFile;
    private final String projectGroupId;
    private final String projectTrigram;
    private final Prompter prompter;
    private final String projectPackage;

    public XSDToDomainClassBuilder(File xsdFile, String projectGroupId, String projectTrigram, Prompter prompter, String projectPackage) {
        this.xsdFile = xsdFile;
        this.projectGroupId = projectGroupId;
        this.projectTrigram = projectTrigram;
        this.prompter = prompter;
        this.projectPackage = projectPackage;
    }

    public String buildDomainObject(String outputDir) throws IOException, PrompterException {
        // Configure sources & output
        String schemaPath = xsdFile.getAbsolutePath();
        String outputDirectory = outputDir + "/app-template/"
                + "app-domain/target/generated-sources/cxf";

        File generatedFiles = new File(outputDirectory);
        generatedFiles.mkdirs();
        final String generatedFilePakage = projectGroupId + "." + projectTrigram + ".jaxb";
        final String domainFilePakage = projectGroupId + "." + projectTrigram + ".domain";
        
        // Setup schema compiler
        SchemaCompiler sc = XJC.createSchemaCompiler();
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
        return prompter.prompt("Please select the resource to manage", possibleResources);
    }

    private String getFolderFromPackage() {
        return getFolderFromPackage(projectPackage);
    }

    private String getFolderFromPackage(final String packageName) {
        return packageName.replaceAll("\\.", "/");
    }
}
