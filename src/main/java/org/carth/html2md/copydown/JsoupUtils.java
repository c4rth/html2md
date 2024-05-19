package org.carth.html2md.copydown;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

// Methods missing from jsoup
class JsoupUtils {

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
}