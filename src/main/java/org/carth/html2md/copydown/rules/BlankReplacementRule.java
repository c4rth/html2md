package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.JsoupUtils;

public class BlankReplacementRule extends Rule {

    public BlankReplacementRule() {
        init(
                (node, options) -> JsoupUtils.isBlank(node),
                (content, node, options) -> JsoupUtils.isBlock(node) ? "\n\n" : ""
        );
    }

}
