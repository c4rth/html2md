package org.carth.html2md.copydown.rules;

public class BrRule extends Rule {

    public BrRule() {
        setRule(
                "br",
                (content, node, options) -> options.br + "\n");
    }

}
