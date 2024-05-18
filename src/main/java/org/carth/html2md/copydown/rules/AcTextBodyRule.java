package org.carth.html2md.copydown.rules;

public class AcTextBodyRule extends Rule {

    public AcTextBodyRule() {
        setRule(
                new String[]{"ac:rich-text-body"},
                (content, node, options) -> content
        );
    }
}
