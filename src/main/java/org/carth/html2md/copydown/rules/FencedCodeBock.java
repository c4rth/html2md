package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.carth.html2md.copydown.style.CodeBlockStyle;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FencedCodeBock extends Rule {

    public FencedCodeBock() {
        setRule(
                (node, options) -> options.codeBlockStyle == CodeBlockStyle.FENCED
                        && node.nodeName().equals("pre")
                        && node.childNodeSize() > 0
                        && node.childNode(0).nodeName().equals("code"),
                this::replace
        );
    }

    private String replace(String content, Node node, Options options) {
        String childClass = node.childNode(0).attr("class");
        if (childClass == null) {
            childClass = "";
        }
        Matcher languageMatcher = Pattern.compile("language-(\\S+)").matcher(childClass);
        String language = "";
        if (languageMatcher.find()) {
            language = languageMatcher.group(1);
        }

        String code;
        if (node.childNode(0) instanceof Element) {
            code = ((Element) node.childNode(0)).wholeText();
        } else {
            code = node.childNode(0).outerHtml();
        }

        String fenceChar = options.fence.substring(0, 1);
        int fenceSize = 3;
        Matcher fenceMatcher = Pattern.compile("(?m)^(" + fenceChar + "{3,})").matcher(content);
        while (fenceMatcher.find()) {
            String group = fenceMatcher.group(1);
            fenceSize = Math.max(group.length() + 1, fenceSize);
        }
        String fence = String.join("", Collections.nCopies(fenceSize, fenceChar));
        if (!code.isEmpty() && code.charAt(code.length() - 1) == '\n') {
            code = code.substring(0, code.length() - 1);
        }
        return (
                "\n\n" + fence + language + "\n" + code
                        + "\n" + fence + "\n\n"
        );
    }
}
