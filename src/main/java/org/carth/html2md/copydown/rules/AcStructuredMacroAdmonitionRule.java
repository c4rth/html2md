package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroAdmonitionRule extends Rule {
    public AcStructuredMacroAdmonitionRule() {
        setRule(
                (element, options) -> element.nodeName().equals("ac:structured-macro")
                        && (element.attr("ac:name").equals("info") || element.attr("ac:name").equals("warning")),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> title = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("title")).findFirst();
                    String titlePart = title.map(Element::wholeText).orElse("");
                    return "\n!!! " + node.attr("ac:name") + " \"" + titlePart + "\"" + content.replaceAll("\n", "\n    ");
                }
        );
    }
}