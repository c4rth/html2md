package org.carth.html2md.report;

import org.carth.html2md.log.Loggable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversionReport implements Loggable {

    private static final Logger logger = LoggerFactory.getLogger(ConversionReport.class);

    @Override
    public Logger getLogger() {
        return logger;
    }

    private static ConversionReport instance = null;
    private final Map<String, List<Report>> reports = new HashMap<>();
    private String current = "";

    private ConversionReport() {
    }

    public static ConversionReport getInstance() {
        if (instance == null) {
            instance = new ConversionReport();
        }
        return instance;
    }

    public void newPage(String name) {
        this.current = name;
        reports.put(current, new ArrayList<>());
    }

    public void addError(String description) {
        this.reports.get(current).add(new Report(ReportType.ERROR, description));
    }

    public void addWarning(String description) {
        this.reports.get(current).add(new Report(ReportType.WARNING, description));
    }

    public void addInfo(String description) {
        this.reports.get(current).add(new Report(ReportType.INFO, description));
    }

    public void report() {
        logInfoLn("{}Conversion report:{}", AnsiColors.CYAN, AnsiColors.RESET);
        reports.forEach((key, value) -> {
            logInfoLn("- {}:", key);
            if (value.isEmpty()) {
                logInfoLn("{}", getMessage(ReportType.OK, "OK"));
            } else {
                value.forEach(report -> {
                    logInfoLn("{}", getMessage(report.type(), report.description()));
                });
            }
        });
    }

    private String getMessage(ReportType type, String message) {
        return " %s %s %s%s".formatted(type.getColor(), type.getText(), message, AnsiColors.RESET);
    }

}
