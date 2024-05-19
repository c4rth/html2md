package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Element;

import java.util.Optional;

@Slf4j
public class AcStructuredMacroCodeRule extends Rule {

    public AcStructuredMacroCodeRule() {
        init(
                (node, options) -> CopyNode.isAcMacroWithName(node, "code"),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> title = CopyNode.getAcParametertWithName((Element) node, "title");
                    Optional<Element> language = CopyNode.getAcParametertWithName((Element) node, "language");
                    Optional<Element> collapse = CopyNode.getAcParametertWithName((Element) node, "collapse");
                    Element code = element.select("ac|plain-text-body").first();
                    String titlePart = title.map(Element::text).orElse("");
                    String languagePart = language.map(Element::wholeText).orElse("");
                    boolean collapsed = collapse.map(Element::wholeText).orElse("false").equals("true");
                    String codePart = code.wholeText();
                    if (collapsed) {
                        return options.collapsedAdmonition + " code \"" + (titlePart.isEmpty() ? "Code" : titlePart) + "\"\n" +
                                ("\n"+ options.fence + languagePart + "\n" + codePart + "\n" + options.fence + "\n").replaceAll("\n", "\n    ");
                    }
                    return (titlePart.isEmpty() ? "" : "\n**" + titlePart + "**\n\n") +
                            options.fence + languagePart + "\n" + codePart + "\n" + options.fence + "\n";
                }
        );
    }
}