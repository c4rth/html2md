package org.carth.html2md.copydown;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Methods missing from jsoup
class NodeUtils {
    private static final Logger log = LoggerFactory.getLogger(NodeUtils.class);

    static boolean isNodeType1(Node element) {
        return element instanceof Element;
    }

    static boolean isNodeType3(Node element) {
        return element.nodeName().equals("text") || element.nodeName().equals("#text");
    }

    // CDATA section node
    static boolean isNodeType4(Node element) {
        return element.nodeName().equals("#cdata");
    }
}