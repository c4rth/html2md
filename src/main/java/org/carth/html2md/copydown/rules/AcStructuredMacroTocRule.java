package org.carth.html2md.copydown.rules;

import static org.carth.html2md.copydown.JsoupUtils.isAcMacro;

public class AcStructuredMacroTocRule extends Rule {
    public AcStructuredMacroTocRule() {
        init(
                (node, options) -> isAcMacro(node, "toc"),
                (content, node, options) -> {
                    return "[TOC]\n";
                }
        );
    }
}