package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import java.util.Optional;

@Slf4j
public class AcStructuredMacroCodeRule extends Rule {

    public AcStructuredMacroCodeRule() {
        setRule(
                (element, options) -> element.nodeName().equals("ac:structured-macro") && element.attr("ac:name").equals("code"),
                (content, node, options) -> {
                    Element element = (Element) node;
                    Optional<Element> title = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("title")).findFirst();
                    Optional<Element> language = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("language")).findFirst();
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