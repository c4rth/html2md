package org.carth.html2md.copydown;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.regex.Pattern;

import static org.carth.html2md.copydown.JsoupUtils.isBlank;
import static org.carth.html2md.copydown.JsoupUtils.isBlock;

public class CopyNode {
    public final Node node;
    private CopyNode parent;

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