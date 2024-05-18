package org.carth.html2md.copydown.rules;

public class AcParameterRule extends Rule {

    public AcParameterRule() {
        setRule(
                "ac:parameter",
                (content, node, options) -> ""
        );
    }

}
