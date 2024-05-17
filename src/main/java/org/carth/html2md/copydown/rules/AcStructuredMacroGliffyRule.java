package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Optional;

public class AcStructuredMacroGliffyRule extends Rule {
    public AcStructuredMacroGliffyRule() {
        super(
                (element) -> element.nodeName().equals("ac:structured-macro")
                        && element.attr("ac:name").equals("gliffy")
        );
    }

    @Override
    public String replacement(String content, Node node) {
        Element element = (Element) node;
        Optional<Element> alt = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("displayName")).findFirst();
        Optional<Element> src = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("name")).findFirst();
        String altPart = alt.map(Element::wholeText).orElse("");
        String srcPart = src.map(Element::wholeText).orElse("");
        return "![" + altPart + "]" + "(attachments/" + srcPart + ".png)";
    }
}