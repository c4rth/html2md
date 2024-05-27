package org.carth.html2md.copydown;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.carth.html2md.copydown.CopyNode.*;

public class JsoupUtils {

    private static final String AC_NAME = "ac:name";

    private JsoupUtils() {
    }

    static boolean isNodeTypeElement(Node node) {
        return node instanceof Element;
    }

    static boolean isNodeTypeText(Node node) {
        return node.nodeName().equals("text") || node.nodeName().equals("#text");
    }

    // CDATA section node
    static boolean isNodeTypeCData(Node node) {
        return node.nodeName().equals("#cdata");
    }

    public static boolean isBlank(Node node) {
        String textContent;
        if (node instanceof Element element) {
            textContent = element.wholeText();
        } else {
            textContent = node.outerHtml();
        }
        return !isVoid(node) &&
                !isMeaningfulWhenBlank(node) &&
                // TODO check text is the same as textContent in browser
                Pattern.compile("(?i)^\\s*$").matcher(textContent).find() &&
                !hasVoidNodesSet(node) &&
                !hasMeaningfulWhenBlankNodesSet(node);
    }

    private static boolean hasVoidNodesSet(Node node) {
        if (!(node instanceof Element element)) {
            return false;
        }
        for (String tagName : VOID_ELEMENTS_SET) {
            if (!element.getElementsByTag(tagName).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVoid(Node node) {
        return VOID_ELEMENTS_SET.contains(node.nodeName());
    }

    private static boolean hasMeaningfulWhenBlankNodesSet(Node node) {
        if (!(node instanceof Element element)) {
            return false;
        }
        for (String tagName : MEANINGFUL_WHEN_BLANK_ELEMENTS_SET) {
            if (!element.getElementsByTag(tagName).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMeaningfulWhenBlank(Node node) {
        return MEANINGFUL_WHEN_BLANK_ELEMENTS_SET.contains(node.nodeName());
    }

    public static boolean isBlock(Node node) {
        return BLOCK_ELEMENTS_SET.contains(node.nodeName()) || isAcBlock(node);
    }

    public static boolean isAcBlock(Node node) {
        return node.nodeName().startsWith("ac:") || node.nodeName().startsWith("ri:");
    }

    public static Optional<Element> getAcParameter(Node node, String attributeValue) {
        return node instanceof Element ?
                ((Element) node).select("ac|parameter").stream().filter(el -> el.hasAttr(AC_NAME) && el.attr(AC_NAME).equals(attributeValue)).findFirst()
                : Optional.empty();
    }

    public static boolean isAcMacro(Node node, String... attrValues) {
        return node.nodeName().equals("ac:structured-macro") && Arrays.asList(attrValues).contains(node.attr(AC_NAME));
    }

    public static String cleanAttribute(String attribute) {
        return attribute.replaceAll("(\n+\\s*)+", "\n");
    }
}