package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroStatusRule extends Rule {
    public AcStructuredMacroStatusRule() {
        setRule(
                (element, options) -> element.nodeName().equals("ac:structured-macro") && element.attr("ac:name").equals("status"),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> color = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("colour")).findFirst();
                    Optional<Element> title = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("title")).findFirst();
                    String colorPart = color.map(Element::wholeText).orElse("");
                    String titlePart = title.map(Element::wholeText).orElse("");
                    return colorPart.isEmpty() ? titlePart :
                            "<span style=\"background-color:" + colorPart + "; color: white\">" + titlePart + "</span>";
                }
        );
    }
}