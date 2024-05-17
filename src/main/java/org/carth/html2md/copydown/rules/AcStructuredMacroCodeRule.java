package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Optional;

@Slf4j
public class AcStructuredMacroCodeRule extends Rule {

    private final String fence;

    public AcStructuredMacroCodeRule(Options options) {
        super(
                (element) -> element.nodeName().equals("ac:structured-macro")
                        && element.attr("ac:name").equals("code")
        );
        this.fence = options.fence;
    }

    @Override
    public String replacement(String content, Node node) {
        Element element = (Element) node;
        Optional<Element> title = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("title")).findFirst();
        Optional<Element> language = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("language")).findFirst();
        Element code = element.select("ac|plain-text-body").first();
        String titlePart = title.map(Element::text).orElse("");
        String languagePart = language.map(Element::wholeText).orElse("");
        String codePart = code.wholeText();
        return (titlePart.isEmpty() ? "" : "\n**" + titlePart + "**\n\n") +
                fence + languagePart + "\n" + codePart + "\n" + fence + "\n";
    }
}