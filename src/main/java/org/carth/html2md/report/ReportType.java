package org.carth.html2md.report;

import lombok.Getter;

@Getter
public enum ReportType {
    ERROR(AnsiColors.RED, Emoticon.ERROR),
    WARNING(AnsiColors.YELLOW, Emoticon.WARNING),
    INFO(AnsiColors.BLUE, Emoticon.INFO),
    OK(AnsiColors.GREEN, Emoticon.OK);

    private final String color;
    private final String text;

    ReportType(String color, String text) {
        this.color = color;
        this.text = text;
    }

}
