package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.carth.html2md.copydown.style.LinkStyle;
import org.jsoup.nodes.Node;

public class InlineLinkRule extends Rule {

    public InlineLinkRule(Options options) {
        super(
                (element) -> {
                    return options.linkStyle == LinkStyle.INLINED
                            && element.nodeName().equals("a")
                            && !element.attr("href").isEmpty();
                });
    }

    @Override
    public String replacement(String content, Node element) {
        String href = element.attr("href");
        String title = cleanAttribute(element.attr("title"));
        if (!title.isEmpty()) {
            title = " \"" + title + "\"";
        }
        return "[" + content + "](" + href + title + ")";
    }
}
