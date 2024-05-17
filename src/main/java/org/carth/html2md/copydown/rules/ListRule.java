package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class ListRule extends Rule {

    public ListRule() {
        super(new String[]{"ul", "ol"});
    }

    @Override
    public String replacement(String content, Node element) {
        Element parent = (Element) element.parentNode();
        if (parent.nodeName().equals("li") && parent.child(parent.childrenSize() - 1) == element) {
            return "\n" + content;
        } else {
            return "\n\n" + content + "\n\n";
        }
    }
}
