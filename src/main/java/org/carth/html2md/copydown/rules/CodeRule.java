package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Node;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeRule extends Rule {
    public CodeRule() {
        init(
                (node, options) -> {
                    boolean hasSiblings = node.previousSibling() != null || node.nextSibling() != null;
                    assert node.parentNode() != null;
                    boolean isCodeBlock = node.parentNode().nodeName().equals("pre") && !hasSiblings;
                    return node.nodeName().equals("code") && !isCodeBlock;
                },
                this::replace
        );
    }

    private String replace(String content, Node node, Options options) {
        if (content.trim().isEmpty()) {
            return "";
        }
        String delimiter = options.preformatted;
        String leadingSpace = "";
        String trailingSpace = "";
        Pattern pattern = Pattern.compile("(?m)(`)+");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            if (Pattern.compile("^`").matcher(content).find()) {
                leadingSpace = " ";
            }
            if (Pattern.compile("`$").matcher(content).find()) {
                trailingSpace = " ";
            }
            int counter = 1;
            do {
                if (delimiter.equals(matcher.group())) {
                    counter++;
                }
            } while (matcher.find());
            delimiter = String.join("", Collections.nCopies(counter, "`"));
        }
        return delimiter + leadingSpace + content + trailingSpace + delimiter;
    }
}
