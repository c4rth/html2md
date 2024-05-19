package org.carth.html2md.copydown.rules;

public class AcBlankRule extends Rule {
    public AcBlankRule() {
        init(
                (node, options) -> node.nodeName().startsWith("ac:") || node.nodeName().startsWith("ri:"),
                (content, node, options) -> ""
        );
    }
}
