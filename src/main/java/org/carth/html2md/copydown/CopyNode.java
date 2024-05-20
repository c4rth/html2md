package org.carth.html2md.copydown;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class CopyNode {
    private static final String[] VOID_ELEMENTS = {
            "area", "base", "br", "col", "command", "embed", "hr", "img", "input",
            "keygen", "link", "meta", "param", "source.test", "track", "wbr"
    };
    private static final String[] MEANINGFUL_WHEN_BLANK_ELEMENTS = {
            "a", "table", "thead", "tbody", "tfoot", "th", "td", "iframe", "script",
            "audio", "video"
    };

    private static final String[] BLOCK_ELEMENTS = {
            "address", "article", "aside", "audio", "blockquote", "body", "canvas",
            "center", "dd", "dir", "div", "dl", "dt", "fieldset", "figcaption", "figure",
            "footer", "form", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "header",
            "hgroup", "hr", "html", "isindex", "li", "main", "menu", "nav", "noframes",
            "noscript", "ol", "output", "p", "pre", "section", "table", "tbody", "td",
            "tfoot", "th", "thead", "tr", "colgroup", "col", "ul"
    };

    private static final Set<String> VOID_ELEMENTS_SET = new HashSet<>(Arrays.asList(VOID_ELEMENTS));
    private static final Set<String> MEANINGFUL_WHEN_BLANK_ELEMENTS_SET = new HashSet<>(Arrays.asList(MEANINGFUL_WHEN_BLANK_ELEMENTS));
    private static final Set<String> BLOCK_ELEMENTS_SET = new HashSet<>(Arrays.asList(BLOCK_ELEMENTS));
    private static final String AC_NAME = "ac:name";

    final Node node;
    CopyNode parent;

    CopyNode(String input) {
        // DOM parsers arrange elements in the <head> and <body>.
        // Wrapping in a custom element ensures elements are reliably arranged in
        // a single element.
        Document document = Jsoup.parse("<x-copydown id=\"copydown-root\">" + input + "</x-copydown>");
        Element root = document.getElementById("copydown-root");
        assert root != null;
        new WhitespaceCollapser().collapse(root);
        this.node = root;
    }

    CopyNode(Node node, CopyNode parent) {
        this.node = node;
        this.parent = parent;
    }

    public static boolean isBlank(Node node) {
        String textContent;
        if (node instanceof Element element ) {
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

    public static Optional<Element> getAcParametertWithName(Element element, String attributeValue) {
        return element.select("ac|parameter").stream().filter(el -> el.hasAttr(AC_NAME) && el.attr(AC_NAME).equals(attributeValue)).findFirst();
    }

    public static boolean isAcMacroWithName(Node node, String... attrValues) {
        return node.nodeName().equals("ac:structured-macro") && Arrays.asList(attrValues).contains(node.attr(AC_NAME));
    }

    public static String cleanAttribute(String attribute) {
        return attribute.replaceAll("(\n+\\s*)+", "\n");
    }

    public boolean isCode() {
        // TODO cache in property to avoid escalating to root
        return this.node.nodeName().equals("code") || (parent != null && parent.isCode());
    }

    public FlankingWhiteSpaces flankingWhitespace() {
        String leading = "";
        String trailing = "";
        if (!isBlock(this.node)) {
            String textContent;
            if (this.node instanceof Element element) {
                textContent = element.wholeText();
            } else {
                textContent = this.node.outerHtml();
            }
            // Don't put extra spaces for a line break
            if (textContent.equals("\n")) {
                return new FlankingWhiteSpaces("", "");
            }
            // TODO original uses textContent
            boolean hasLeading = Pattern.compile("^\\s").matcher(textContent).find();
            boolean hasTrailing = Pattern.compile("\\s$").matcher(textContent).find();
            // TODO maybe make node property and avoid recomputing
            boolean blankWithSpaces = isBlank(this.node) && hasLeading && hasTrailing;
            if (hasLeading && !isLeftFlankedByWhitespaces()) {
                leading = " ";
            }
            if (!blankWithSpaces && hasTrailing && !isRightFlankedByWhitespaces()) {
                trailing = " ";
            }
        }
        return new FlankingWhiteSpaces(leading, trailing);
    }

    private boolean isLeftFlankedByWhitespaces() {
        return isChildFlankedByWhitespaces(" $", this.node.previousSibling());
    }

    private boolean isRightFlankedByWhitespaces() {
        return isChildFlankedByWhitespaces("^ ", this.node.nextSibling());
    }

    private boolean isChildFlankedByWhitespaces(String regex, Node sibling) {
        if (sibling == null) {
            return false;
        }
        if (JsoupUtils.isNodeTypeText(sibling)) {
            // TODO fix. Originally sibling.nodeValue
            return Pattern.compile(regex).matcher(sibling.outerHtml()).find();
        }
        if (JsoupUtils.isNodeTypeElement(sibling)) {
            // TODO fix. Originally textContent
            return Pattern.compile(regex).matcher(sibling.outerHtml()).find();
        }
        return false;
    }

    public record FlankingWhiteSpaces(String leading, String trailing) {
    }
}