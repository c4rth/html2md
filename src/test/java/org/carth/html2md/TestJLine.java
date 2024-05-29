package org.carth.html2md;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

public class TestJLine {

    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.terminal();

        for (int i = 0; i <= 100; i += 10) {
            terminal.puts(InfoCmp.Capability.cursor_home);
            terminal.writer().print("Progress: " + i + "%");
            terminal.flush();
            Thread.sleep(500);
        }
        terminal.writer().println();
    }
}
