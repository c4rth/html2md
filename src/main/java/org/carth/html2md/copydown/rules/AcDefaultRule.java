package org.carth.html2md.copydown.rules;

public class AcDefaultRule extends Rule {
    public AcDefaultRule() {
        setRule(
                new String[]{"ac:layout", "ac:layout-section", "ac:layout-cell"},
                (content, node, options) -> content
        );
    }

}
