package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import static org.carth.html2md.copydown.JsoupUtils.getAcParameter;
import static org.carth.html2md.copydown.JsoupUtils.isAcMacro;

public class AcStructuredMacroGliffyRule extends Rule {
    public AcStructuredMacroGliffyRule() {
        init(
                (node, options) -> isAcMacro(node, "gliffy"),
                (content, node, options) -> {
                    String altPart = getAcParameter(node, "displayName").map(Element::wholeText).orElse("");
                    String srcPart = getAcParameter(node, "name").map(Element::wholeText).orElse("");
                    return "![" + altPart + "]" + "(attachments/" + srcPart + ".png)";
                }
        );
    }
}