package org.carth.html2md.copydown.rules;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeRule extends Rule {
    public CodeRule() {
        setRule(
                (element, options) -> {
                    boolean hasSiblings = element.previousSibling() != null || element.nextSibling() != null;
                    assert element.parentNode() != null;
                    boolean isCodeBlock = element.parentNode().nodeName().equals("pre") && !hasSiblings;
                    return element.nodeName().equals("code") && !isCodeBlock;
                },
                (content, node, options) -> {
                    if (content.trim().isEmpty()) {
                        return "";
                    }
                    String delimiter = "`";
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
        );
    }
}
