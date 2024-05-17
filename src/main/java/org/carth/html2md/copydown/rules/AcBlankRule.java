package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;

public class AcBlankRule extends Rule {
    public AcBlankRule() {
        super((element) -> element.nodeName().startsWith("ac:") || element.nodeName().startsWith("ri:"));
    }

    @Override
    public String replacement(String content, Node element) {
        return "";
    }
}
