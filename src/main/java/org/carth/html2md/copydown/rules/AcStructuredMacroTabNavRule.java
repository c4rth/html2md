package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import static org.carth.html2md.copydown.JsoupUtils.getAcParameter;
import static org.carth.html2md.copydown.JsoupUtils.isAcMacro;

public class AcStructuredMacroTabNavRule extends Rule {
    public AcStructuredMacroTabNavRule() {
        init(
                (node, options) -> isAcMacro(node, "tab-pane", "horizontal-nav-item"),
                (content, node, options) -> {
                    String titlePart = getAcParameter(node, "name").map(Element::text).orElse("Tab");
                    return "\n=== \"<span style=\"font-size: 1rem\">" + titlePart + "</span>\"" + content.replace("\n", "\n    ");
                }
        );
    }
}