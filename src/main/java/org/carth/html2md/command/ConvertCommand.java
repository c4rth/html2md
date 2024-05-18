package org.carth.html2md.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.carth.html2md.Html2mdApplication;
import org.carth.html2md.copydown.CopyDown;
import org.carth.html2md.utils.FileUtils;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Component
@Command(name = "convert", mixinStandardHelpOptions = true, sortOptions = false)
@Slf4j
@RequiredArgsConstructor
public class ConvertCommand implements Callable<Integer> {

    private final CopyDown copyDown;
    @Option(order = -100, names = {"-p", "--page"}, description = "page id", required = true)
    private String page;
    @Option(order = -99, names = {"-d"}, description = "debug", defaultValue = "false")
    private boolean debug;

    @Override
    public Integer call() {
        setLogLevel(debug);
        String filename = page + ".txt";
        log.info("Converting '%s' to markdown.".formatted(filename));
        String html = FileUtils.readFile("./files/source/" + filename);
        if (html == null) {
            log.error("'%s' not found or empty".formatted(filename));
            return -1;
        }
        String markdown = convertHtml2Markdown(html);
        String outputFilename = FileUtils.writeFile("./files/result/" + filename, markdown);
        log.info("Converted markdown: " + outputFilename);
        log.info("Done");
        return 0;
    }

    private String convertHtml2Markdown(String html) {
        return copyDown.convert(html);
    }

    private void setLogLevel(boolean debug) {
        log.info("debug? " + debug);
        LoggingSystem system = LoggingSystem.get(Html2mdApplication.class.getClassLoader());
        system.setLogLevel("org.carth", debug ? LogLevel.TRACE : LogLevel.INFO);
    }
}