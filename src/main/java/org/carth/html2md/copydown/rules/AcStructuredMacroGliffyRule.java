package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroGliffyRule extends Rule {
    public AcStructuredMacroGliffyRule() {
        init(
                (node, options) -> CopyNode.isAcMacroWithName(node, "gliffy"),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> alt = CopyNode.getAcParametertWithName(element, "displayName");
                    Optional<Element> src = CopyNode.getAcParametertWithName(element, "name");
                    String altPart = alt.map(Element::wholeText).orElse("");
                    String srcPart = src.map(Element::wholeText).orElse("");
                    return "![" + altPart + "]" + "(attachments/" + srcPart + ".png)";
                }
        );
    }
}