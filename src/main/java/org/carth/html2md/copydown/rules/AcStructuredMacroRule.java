package org.carth.html2md.copydown.rules;

public class AcStructuredMacroRule extends Rule {
    public AcStructuredMacroRule() {
        setRule(
                "ac:structured-macro",
                (content, node, options) -> content
        );
    }

}
