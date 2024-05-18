package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcEmoticonRule extends Rule {

    private static final Logger log = LoggerFactory.getLogger(AcEmoticonRule.class);

    public AcEmoticonRule() {
        super(
                "ac:emoticon"
        );
    }

    @Override
    public String replacement(String content, Node element) {
        return EmojiMap.getEmoji(element.attr("ac:emoji-id")) + " ";
    }
}
