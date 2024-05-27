package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import static org.carth.html2md.copydown.JsoupUtils.getAcParameter;
import static org.carth.html2md.copydown.JsoupUtils.isAcMacro;

public class AcStructuredMacroAdmonitionRule extends Rule {
    public AcStructuredMacroAdmonitionRule() {
        init(
                (node, options) -> isAcMacro(node, "info", "warning", "note", "tip"),
                (content, node, options) -> {
                    String titlePart = getAcParameter(node, "title").map(Element::wholeText).orElse("");
                    boolean collapsed = getAcParameter(node, "collapse").map(Element::wholeText).orElse("false").equals("true");
                    return "\n" + (collapsed ? options.collapsedAdmonition : options.admonition) + " " + node.attr("ac:name") + " \""
                            + titlePart + "\"" + content.replace("\n", "\n    ");
                }
        );
    }
}