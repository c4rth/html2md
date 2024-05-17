package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.carth.html2md.copydown.style.LinkReferenceStyle;
import org.carth.html2md.copydown.style.LinkStyle;
import org.jsoup.nodes.Node;

import java.util.List;
import java.util.function.Supplier;

public class ReferenceLinkRule extends Rule {

    private final LinkReferenceStyle linkReferenceStyle;
    private final List<String> references;

    public ReferenceLinkRule(Options options, List<String> references) {
        super(
                (element) -> {
                    return options.linkStyle == LinkStyle.REFERENCED
                            && element.nodeName().equals("a")
                            && !element.attr("href").isEmpty();
                });
        this.linkReferenceStyle = options.linkReferenceStyle;
        this.references = references;
    }

    @Override
    public String replacement(String content, Node element) {
        String href = element.attr("href");
        String title = cleanAttribute(element.attr("title"));
        if (!title.isEmpty()) {
            title = " \"" + title + "\"";
        }
        String replacement;
        String reference;
        switch (linkReferenceStyle) {
            case COLLAPSED:
                replacement = "[" + content + "][]";
                reference = "[" + content + "]: " + href + title;
                break;
            case SHORTCUT:
                replacement = "[" + content + "]";
                reference = "[" + content + "]: " + href + title;
                break;
            case DEFAULT:
            default:
                int id = references.size() + 1;
                replacement = "[" + content + "][" + id + "]";
                reference = "[" + id + "]: " + href + title;
        }
        references.add(reference);
        return replacement;
    }

    @Override
    public Supplier<String> getAppend() {
        return () -> {
            String referenceString = "";
            if (!references.isEmpty()) {
                referenceString = "\n\n" + String.join("\n", references) + "\n\n";
            }
            return referenceString;
        };
    }
}
