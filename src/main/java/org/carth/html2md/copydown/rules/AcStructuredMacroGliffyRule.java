package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroGliffyRule extends Rule {
    public AcStructuredMacroGliffyRule() {
        setRule(
                (node, options) -> CopyNode.isAcMacroWithName(node, "gliffy"),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> alt = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("displayName")).findFirst();
                    Optional<Element> src = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("name")).findFirst();
                    String altPart = alt.map(Element::wholeText).orElse("");
                    String srcPart = src.map(Element::wholeText).orElse("");
                    return "![" + altPart + "]" + "(attachments/" + srcPart + ".png)";
                }
        );
    }
}