package org.carth.html2md.copydown.rules;

import java.util.List;

public class BrRule extends Rule {

    public BrRule() {
        init(
                List.of("br", "br\\"),
                (content, node, options) -> options.br + "\n");
    }

}
