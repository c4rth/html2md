package org.carth.html2md.copydown.rules;

public class AcEmoticonRule extends Rule {

    public AcEmoticonRule() {
        setRule(
                "ac:emoticon",
                (content, node, options) -> node.nodeName() + EmojiMap.getEmoji(node.attr("ac:emoji-id")) + " "
        );
    }
}
