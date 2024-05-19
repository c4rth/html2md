package org.carth.html2md.copydown;

import lombok.Builder;
import org.carth.html2md.copydown.style.CodeBlockStyle;
import org.carth.html2md.copydown.style.HeadingStyle;
import org.carth.html2md.copydown.style.LinkReferenceStyle;
import org.carth.html2md.copydown.style.LinkStyle;

@Builder
public class Options {

    @Builder.Default
    public  String br = "  ";
    @Builder.Default
    public  String hr = "* * *";
    @Builder.Default
    public  String emDelimiter = "_";
    @Builder.Default
    public  String strongDelimiter = "**";
    @Builder.Default
    public  HeadingStyle headingStyle = HeadingStyle.SETEXT;
    @Builder.Default
    public  String bulletListMaker = "-";
    @Builder.Default
    public  CodeBlockStyle codeBlockStyle = CodeBlockStyle.INDENTED;
    @Builder.Default
    public  LinkStyle linkStyle = LinkStyle.INLINED;
    @Builder.Default
    public  LinkReferenceStyle linkReferenceStyle = LinkReferenceStyle.DEFAULT;
    @Builder.Default
    public  String fence = "```";
    @Builder.Default
    public String admonition = "!!!";
    @Builder.Default
    public String collapsedAdmonition = "???";
}