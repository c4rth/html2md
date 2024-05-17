package org.carth.html2md.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {

    public static String readFile(String filename) {
        String html = null;
        try {
            File resource = new File(filename);
            html = new String(Files.readAllBytes(resource.toPath()));
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
        return html;
    }

    public static String writeFile(String filename, String markdown) {
        try {
            Path path = Paths.get(filename+ ".md");
            Files.writeString(path, markdown);
            return path.toAbsolutePath().toString();
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            return null;
        }
    }
}
