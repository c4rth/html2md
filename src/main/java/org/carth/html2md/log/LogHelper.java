package org.carth.html2md.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

@Component
public class LogHelper {

    @Value("${app.logging.pattern.normal}")
    private String normalLogPattern;
    @Value("${app.logging.pattern.debug}")
    private String debugLogPattern;

    public void setLogLevel(boolean debug) {

        TerminalLogger.init(debug);
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
}
