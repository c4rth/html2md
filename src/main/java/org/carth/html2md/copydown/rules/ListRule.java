package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

public class ListRule extends Rule {

    public ListRule() {
        setRule(
                new String[]{"ul", "ol"},
                (content, node, options) -> {
                    Element parent = (Element) node.parentNode();
                    if (parent.nodeName().equals("li") && parent.child(parent.childrenSize() - 1) == node) {
                        return "\n" + content;
                    } else {
                        return "\n\n" + content + "\n\n";
                    }
                }
        );
    }
}
