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
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author Mickael Dubois
 */
public class JsonSchemaToDomainClassBuilder {

    private final File jsonSchemaFile;
    private final String projectGroupId;
    private final String projectTrigram;
    private final Prompter prompter;
    private final String projectPackage;

    public JsonSchemaToDomainClassBuilder(File jsonSchemaFile, String projectGroupId, String projectTrigram, Prompter prompter, String projectPackage) {
        this.jsonSchemaFile = jsonSchemaFile;
        this.projectGroupId = projectGroupId;
        this.projectTrigram = projectTrigram;
        this.prompter = prompter;
        this.projectPackage = projectPackage;
    }

    public String buildDomainObject(String outputDir) throws IOException, PrompterException {
        // Configure sources & output
        String schemaPath = jsonSchemaFile.getAbsolutePath();
        String outputDirectory = outputDir + "/app-template/"
                + "app-domain/target/generated-sources/cxf";

        File generatedFiles = new File(outputDirectory);
        generatedFiles.mkdirs();
        final String generatedFilePakage = projectGroupId + "." + projectTrigram + ".jaxb";
        final String domainFilePakage = projectGroupId + "." + projectTrigram + ".domain";

        JCodeModel codeModel = new JCodeModel();

        URL source = new URL(schemaPath);

        GenerationConfig config = new DefaultGenerationConfig() {

            @Override
            public boolean isIncludeHashcodeAndEquals() {// set config option by overriding method
                return false;
            }
        };

        SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(), new SchemaStore()), new SchemaGenerator());
        mapper.generate(codeModel, "ClassName", "com.example", source);

        codeModel.build(new File(outputDirectory));


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

        
        String resourceToManage = jsonSchemaFile.getName().replace(".jsonschema", "");
        char c[] = resourceToManage.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        resourceToManage = new String(c);
        return resourceToManage;
    }

    private String getFolderFromPackage() {
        return getFolderFromPackage(projectPackage);
    }

    private String getFolderFromPackage(final String packageName) {
        return packageName.replaceAll("\\.", "/");
    }
}
