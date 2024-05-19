package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import java.util.List;

public class ListRule extends Rule {

    public ListRule() {
        init(
                List.of("ul", "ol"),
                (content, node, options) -> {
                    Element parent = (Element) node.parentNode();
                    assert parent != null;
                    if (parent.nodeName().equals("li") && parent.child(parent.childrenSize() - 1) == node) {
                        return "\n" + content;
                    } else {
                        return "\n\n" + content + "\n\n";
                    }
                }
        );
    }
}
