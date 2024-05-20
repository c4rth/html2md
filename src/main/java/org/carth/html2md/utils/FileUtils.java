package org.carth.html2md.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {

    public static String readFile(String filename) throws IOException {
        File resource = new File(filename);
        return new String(Files.readAllBytes(resource.toPath()));
    }

    public static String writeFile(String filename, String markdown) throws IOException {
        Path path = Paths.get(filename + ".md");
        Files.writeString(path, markdown);
        return path.toAbsolutePath().toString();
    }
}
