package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import static org.carth.html2md.copydown.JsoupUtils.getAcParameter;
import static org.carth.html2md.copydown.JsoupUtils.isAcMacro;

public class AcStructuredMacroStatusRule extends Rule {
    public AcStructuredMacroStatusRule() {
        init(
                (node, options) -> isAcMacro(node, "status"),
                (content, node, options) -> {
                    String colorPart = getAcParameter(node, "colour").map(Element::wholeText).orElse("");
                    String titlePart = getAcParameter(node, "title").map(Element::wholeText).orElse("");
                    return colorPart.isEmpty() ?
                            titlePart :
                            "<span style=\"background-color:" + colorPart + "; color: white\">" + titlePart + "</span>";
                }
        );
    }
}