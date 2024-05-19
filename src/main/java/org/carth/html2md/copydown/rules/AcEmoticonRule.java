package org.carth.html2md.copydown.rules;

public class AcEmoticonRule extends Rule {

    public AcEmoticonRule() {
        setRule(
                "ac:emoticon",
                (content, node, options) -> EmojiMap.getEmoji(node.attr("ac:emoji-id")) + " "
        );
    }
}
