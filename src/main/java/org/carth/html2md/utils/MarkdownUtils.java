package org.carth.html2md.utils;

public class MarkdownUtils {

    public static String cleanAttribute(String attribute) {
        return attribute.replaceAll("(\n+\\s*)+", "\n");
    }

}
