package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;

public class AcStructuredMacroRule extends Rule {
    public AcStructuredMacroRule() {
        super("ac:structured-macro");
    }

    @Override
    public String replacement(String content, Node element) {
        return content;
    }
}
