package org.carth.html2md;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert json to java map
 * JSON from <a href="https://github.com/facelessuser/pymdown-extensions/blob/main/pymdownx/emoji1_db.py"></a>
 */
@Slf4j
public class GenerateEmojiMap {

    public static void main(String[] args) throws Exception {
        File emojiFile = new ClassPathResource("emoji.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rawNode = mapper.readTree(emojiFile);
        ObjectNode node = (ObjectNode) rawNode;


        String packageName = "org.carth.html2md.copydown.rules";
        String className = "EmojiMap";

        // Define the Map field
        FieldSpec mapField = FieldSpec.builder(ParameterizedTypeName.get(Map.class, String.class, String.class), "EMOJI_MAP")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T<>()", HashMap.class)
                .build();

        // Define the static block to initialize the map
        CodeBlock.Builder staticBlockBuilder = CodeBlock.builder();
        node.fields().forEachRemaining(f -> {
                    var key = f.getKey();
                    var unicode = f.getValue().get("unicode").asText();
                    log.info("{} = {}", unicode, key);
                    staticBlockBuilder.addStatement("EMOJI_MAP.put($S,$S)", unicode, key);
                }
        );

        CodeBlock staticBlock = staticBlockBuilder.build();

        MethodSpec getValueMethod = MethodSpec.methodBuilder("getEmoji")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(String.class)
                .addParameter(String.class, "key")
                .addStatement("return EMOJI_MAP.get(key)")
                .build();

        TypeSpec generatedClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addField(mapField)
                .addStaticBlock(staticBlock)
                .addMethod(getValueMethod)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
        javaFile.writeTo(Paths.get("src/main/java"));
    }
}
