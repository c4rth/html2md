package org.carth.html2md.copydown.rules;

public class AcBlankRule extends Rule {
    public AcBlankRule() {
        setRule(
                (element, options) -> element.nodeName().startsWith("ac:") || element.nodeName().startsWith("ri:"),
                (content, node, options) -> ""
        );
    }
}
