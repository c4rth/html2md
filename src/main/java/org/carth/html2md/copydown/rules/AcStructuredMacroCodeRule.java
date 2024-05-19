package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.carth.html2md.utils.AcNodeUtils;
import org.jsoup.nodes.Element;

import java.util.Optional;

@Slf4j
public class AcStructuredMacroCodeRule extends Rule {

    public AcStructuredMacroCodeRule() {
        setRule(
                (node, options) -> AcNodeUtils.isAcMacroWithName(node, "code"),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> title = AcNodeUtils.getAcParametertWithName((Element) node, "title");
                    Optional<Element> language = AcNodeUtils.getAcParametertWithName((Element) node, "language");
                    Element code = element.select("ac|plain-text-body").first();
                    String titlePart = title.map(Element::text).orElse("");
                    String languagePart = language.map(Element::wholeText).orElse("");
                    String codePart = code.wholeText();
                    return (titlePart.isEmpty() ? "" : "\n**" + titlePart + "**\n\n") +
                            options.fence + languagePart + "\n" + codePart + "\n" + options.fence + "\n";
                }
        );
    }
}