package org.carth.html2md.copydown.rules;

import java.util.List;

public class TextFormattingRule extends Rule {

    public TextFormattingRule() {
        init(
                List.of("u", "sub", "sup","strong", "b", "em", "i", "pre", "s"),
                (content, node, options) -> {
                    if (content.trim().isEmpty()) {
                        return "";
                    }
                    String delimiter = switch (node.nodeName()) {
                        case "u" -> options.underlineDelimiter;
                        case "sub" -> options.subscriptDelimiter;
                        case "sup" -> options.superscriptDelimiter;
                        case "strong", "b" -> options.strongDelimiter;
                        case "em", "i" -> options.emDelimiter;
                        case "pre" -> options.preformatted;
                        case "s" -> options.strikethroughDelimiter;
                        default -> "";
                    };
                    return delimiter + content + delimiter;
                }
        );
    }

}
