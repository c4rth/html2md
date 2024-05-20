package org.carth.html2md.command;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.carth.html2md.copydown.CopyDown;
import org.carth.html2md.report.ConversionReport;
import org.carth.html2md.utils.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

@Component
@Command(name = "convert", mixinStandardHelpOptions = true, sortOptions = false, versionProvider = ConvertCommand.class)
@Slf4j
@RequiredArgsConstructor
public class ConvertCommand implements Callable<Integer>, CommandLine.IVersionProvider {

    private final BuildProperties buildProperties;
    private final CopyDown copyDown;

    @Option(order = -100, names = {"-p", "--page"}, description = "page id", required = true)
    private String page;
    @Option(order = -99, names = {"-d"}, description = "debug", defaultValue = "false")
    private boolean debug;

    @Value("${app.logging.pattern.normal}")
    private String normalLogPattern;
    @Value("${app.logging.pattern.debug}")
    private String debugLogPattern;

    @Override
    public Integer call() {
        long start = System.nanoTime();
        setLogLevel(debug);
        String filename = page + ".txt";
        log.trace("Version: {}:{} - {} - {}", buildProperties.getGroup(), buildProperties.getArtifact(), buildProperties.getVersion(), buildProperties.getTime());
        log.info("Converting '{}' to markdown.", filename);

        String html;
        try {
            html = FileUtils.readFile("./files/source/" + filename);
        } catch (IOException e) {
            log.error("{} - {}", e.getClass().getCanonicalName(), e.getLocalizedMessage());
            return -1;
        }

        String markdown = convertHtml2Markdown(filename, html);

        String outputFilename;
        try {
            outputFilename = FileUtils.writeFile("./files/result/" + filename, markdown);
        } catch (IOException e) {
            log.error("{} - {}", e.getClass().getCanonicalName(), e.getLocalizedMessage());
            return -1;
        }
        log.info("Converted markdown: {}", outputFilename);

        ConversionReport.getInstance().report();

        long end = System.nanoTime();
        long duration = (end - start) / 1_000_000;
        log.info("Done in {}ms", duration);
        return CommandLine.ExitCode.OK;
    }

    private String convertHtml2Markdown(String name, String html) {
        ConversionReport.getInstance().newPage(name);
        return copyDown.convert(html);
    }

    private void setLogLevel(boolean debug) {

        Logger appLogger = (Logger) LoggerFactory.getLogger("org.carth");
        appLogger.setLevel(debug ? Level.TRACE : Level.INFO);

        Logger rootLogger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(rootLogger.getLoggerContext());
        encoder.setPattern(debug ? debugLogPattern : normalLogPattern);
        encoder.start();
        ConsoleAppender<ILoggingEvent> consoleAppender = (ConsoleAppender<ILoggingEvent>) rootLogger.getAppender("CONSOLE");
        if (consoleAppender != null) {
            consoleAppender.setEncoder(encoder);
            consoleAppender.start();
        }
    }

    @Override
    public String[] getVersion() throws Exception {
        return new String[]{buildProperties.getVersion()};
    }
}