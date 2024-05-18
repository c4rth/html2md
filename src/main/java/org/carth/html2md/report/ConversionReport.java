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
        log.info("Conversion report:");
        reports.forEach((key, value) -> {
            log.info("- {}:", key);
            if (value.isEmpty()) {
                log.info("  ✅ OK");
            } else {
                value.forEach(report -> {
                    if (report.type() == ReportType.ERROR) {
                        log.info(" ⛔ {}", report.description());
                    } else {
                        log.info(" ⚠️ {}", report.description());
                    }
                });
            }
        });
    }

}
