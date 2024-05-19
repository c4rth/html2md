package org.carth.html2md.utils;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Arrays;
import java.util.Optional;

public class AcNodeUtils {

    public static Optional<Element> getAcParametertWithName(Element element, String attributeValue) {
        return element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals(attributeValue)).findFirst();
    }

    public static boolean isAcMacroWithName(Node node, String... attrValues) {
        return node.nodeName().equals("ac:structured-macro") && Arrays.asList(attrValues).contains(node.attr("ac:name"));
    }
}
