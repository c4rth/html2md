package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import static org.carth.html2md.copydown.JsoupUtils.getAcParameter;
import static org.carth.html2md.copydown.JsoupUtils.isAcMacro;

@Slf4j
public class AcStructuredMacroCodeRule extends Rule {

    public AcStructuredMacroCodeRule() {
        init(
                (node, options) -> isAcMacro(node, "code"),
                (content, node, options) -> {
                    Element code = ((Element) node).select("ac|plain-text-body").first();
                    String titlePart = getAcParameter(node, "title").map(Element::text).orElse("");
                    String languagePart = getAcParameter(node, "language").map(Element::wholeText).orElse("");
                    boolean collapsed = getAcParameter(node, "collapse").map(Element::wholeText).orElse("false").equals("true");
                    String codePart = code.wholeText();
                    if (collapsed) {
                        return options.collapsedAdmonition + " code \"" + (titlePart.isEmpty() ? "Code" : titlePart) + "\"\n" +
                                ("\n" + options.fence + languagePart + "\n" + codePart + "\n" + options.fence + "\n").replace("\n", "\n    ");
                    }
                    return (titlePart.isEmpty() ? "" : "\n**" + titlePart + "**\n\n") +
                            options.fence + languagePart + "\n" + codePart + "\n" + options.fence + "\n";
                }
        );
    }
}