package org.carth.html2md.log;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;

import static org.jline.terminal.TerminalBuilder.PROP_PROVIDER_JANSI;

public class TerminalLogger {

    private static final String CUU = "\u001B[A";
    private static final String ERASE_LINE = "\u001B[0K";

    private static TerminalLogger instance = null;

    public boolean debug;

    private Terminal terminal;

    private TerminalLogger(boolean debug) {
        this.debug = debug;
        if (!debug) {
            try {
                terminal = TerminalBuilder.builder().provider(PROP_PROVIDER_JANSI).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void init(boolean debug) {
        instance = new TerminalLogger(debug);
        if (!debug)
            instance.print(true, "", null);
    }

    public static TerminalLogger get() {
        return instance;
    }

    public String getColored(String message, PromptColor color) {
        return (new AttributedStringBuilder()).append(message, AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle())).toAnsi();
    }

    public void println(String message) {
        if (!debug)
            print(true, message, null);
    }

    public void println(String message, PromptColor color) {
        if (!debug)
            print(true, message, color);
    }

    public void print(String message) {
        if (!debug)
            print(false, message, null);
    }

    public void print(String message, PromptColor color) {
        if (!debug)
            print(false, message, color);
    }

    private void print(boolean ln, String message, PromptColor color) {
        String toPrint = message;
        if (color != null) {
            toPrint = getColored(message, color);
        }
        terminal.writer().println(CUU + "\r" + ERASE_LINE + toPrint);
        if (ln) {
            terminal.writer().println("");
        }
        terminal.flush();
    }
}
