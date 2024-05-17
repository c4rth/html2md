package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.carth.html2md.copydown.style.HeadingStyle;
import org.jsoup.nodes.Node;

import java.util.Collections;

public class HeadingRule extends Rule {

    private final HeadingStyle headingStyle;

    public HeadingRule(Options options) {
        super(new String[]{"h1", "h2", "h3", "h4", "h5", "h6"});
        this.headingStyle = options.headingStyle;
    }

    @Override
    public String replacement(String content, Node element) {
        int hLevel = Integer.parseInt(element.nodeName().substring(1, 2));
        if (headingStyle == HeadingStyle.SETEXT && hLevel < 3) {
            String underline = String.join("", Collections.nCopies(content.length(), hLevel == 1 ? "=" : "-"));
            return "\n\n" + content + "\n" + underline + "\n\n";
        } else {
            return "\n\n" + String.join("", Collections.nCopies(hLevel, "#")) + " " + content + "\n\n";
        }
    }
}
