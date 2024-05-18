package org.carth.html2md.copydown;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

// Methods missing from jsoup
class NodeUtils {

    static boolean isNodeType1(Node node) {
        return node instanceof Element;
    }

    static boolean isNodeType3(Node node) {
        return node.nodeName().equals("text") || node.nodeName().equals("#text");
    }

    // CDATA section node
    static boolean isNodeType4(Node node) {
        return node.nodeName().equals("#cdata");
    }
}