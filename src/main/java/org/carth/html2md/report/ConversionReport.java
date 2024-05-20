package org.carth.html2md.report;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConversionReport {
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

    public void report() {
        log.info("{}Conversion report:{}", AnsiColors.CYAN, AnsiColors.RESET);
        reports.forEach((key, value) -> {
            log.info("- {}:", key);
            if (value.isEmpty()) {
                log.info("{}", getMessage(AnsiColors.GREEN, Emoticon.OK, "OK"));
            } else {
                value.forEach(report -> {
                    if (report.type() == ReportType.ERROR) {
                        log.info("{}", getMessage(AnsiColors.RED, Emoticon.ERROR, report.description()));
                    } else {
                        log.info("{}", getMessage(AnsiColors.YELLOW, Emoticon.WARNING, report.description()));
                    }
                });
            }
        });
    }

    private String getMessage(String color, String emoticon, String message) {
        return " %s %s %s%s".formatted(color, emoticon, message, AnsiColors.RESET);
    }

}
