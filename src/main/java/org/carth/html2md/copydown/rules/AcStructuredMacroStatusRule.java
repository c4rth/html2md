package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroStatusRule extends Rule {
    public AcStructuredMacroStatusRule() {
        init(
                (node, options) -> CopyNode.isAcMacroWithName(node, "status"),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> color = CopyNode.getAcParametertWithName(element, "colour");
                    Optional<Element> title = CopyNode.getAcParametertWithName(element, "title");
                    String colorPart = color.map(Element::wholeText).orElse("");
                    String titlePart = title.map(Element::wholeText).orElse("");
                    return colorPart.isEmpty() ?
                            titlePart :
                            "<span style=\"background-color:" + colorPart + "; color: white\">" + titlePart + "</span>";
                }
        );
    }
}