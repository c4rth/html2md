package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.carth.html2md.copydown.style.LinkStyle;
import org.jsoup.nodes.Node;

import java.util.List;

import static org.carth.html2md.copydown.CopyNode.cleanAttribute;

public class ReferenceLinkRule extends Rule {

    private final List<String> references;

    public ReferenceLinkRule(List<String> references) {
        init(
                (node, options) ->
                        options.linkStyle == LinkStyle.REFERENCED
                                && node.nodeName().equals("a")
                                && !node.attr("href").isEmpty(),
                this::replace,
                this::append
        );
        this.references = references;
    }

    private String replace(String content, Node node, Options options) {
        String href = node.attr("href");
        String title = cleanAttribute(node.attr("title"));
        if (!title.isEmpty()) {
            title = " \"" + title + "\"";
        }
        String replacement;
        String reference;
        switch (options.linkReferenceStyle) {
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

    private String append(Options options) {
        String referenceString = "";
        if (!references.isEmpty()) {
            referenceString = "\n\n" + String.join("\n", references) + "\n\n";
        }
        return referenceString;
    }
}
