package org.carth.html2md.copydown.rules;

public class BrRule extends Rule {

    public BrRule() {
        init(
                "br",
                (content, node, options) -> options.br + "\n");
    }

}
