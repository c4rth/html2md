package org.carth.html2md.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.carth.html2md.copydown.CopyDown;
import org.carth.html2md.log.LogHelper;
import org.carth.html2md.log.Loggable;
import org.carth.html2md.report.ConversionReport;
import org.carth.html2md.utils.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Command(name = "convert", mixinStandardHelpOptions = true, sortOptions = false, versionProvider = ConvertCommand.class)
@RequiredArgsConstructor
public class ConvertCommand implements Callable<Integer>, CommandLine.IVersionProvider, Loggable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    @Override
    public org.slf4j.Logger getLogger() {
        return logger;
    }

    private final BuildProperties buildProperties;
    private final CopyDown copyDown;
    private final LogHelper logHelper;

    @Getter
    @Option(order = -100, names = {"-p", "--page"}, description = "page id", required = true)
    private String page;

    @Getter
    @Option(order = -99, names = {"-d"}, description = "debug", defaultValue = "false")
    private boolean debug;

    private Set<String> listFilesO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Integer call() {
        long start = System.nanoTime();
        logHelper.setLogLevel(debug);
        String filename = page + ".txt";
        logTrace("Version: {}:{} - {} - {}", buildProperties.getGroup(), buildProperties.getArtifact(), buildProperties.getVersion(), buildProperties.getTime());
        logInfo("Converting '{}' to markdown.", filename);
        var filenames = listFilesO("./files/source");
        filenames.forEach(this::convert);
        /*
        return convert(filename);
         */
        ConversionReport.getInstance().report();

        long end = System.nanoTime();
        long duration = (end - start) / 1_000_000;
        logInfoLn("Done in {}ms", duration);
        return CommandLine.ExitCode.OK;
    }

    private Integer convert(String filename) {
        String html;
        try {
            html = FileUtils.readFile("./files/source/" + filename);
        } catch (IOException e) {
            logError("{} - {}", e.getClass().getCanonicalName(), e.getLocalizedMessage());
            return -1;
        }

        String markdown = convertHtml2Markdown(filename, html);

        String outputFilename;
        try {
            outputFilename = FileUtils.writeFile("./files/result/" + filename + ".md", markdown);
        } catch (IOException e) {
            logError("{} - {}", e.getClass().getCanonicalName(), e.getLocalizedMessage());
            return -1;
        }
        logInfoLn("Converted markdown: {}", outputFilename);

        return CommandLine.ExitCode.OK;
    }

    private String convertHtml2Markdown(String name, String html) {
        ConversionReport.getInstance().newPage(name);
        return copyDown.convert(html);
    }


    @Override
    public String[] getVersion() throws Exception {
        return new String[]{buildProperties.getVersion()};
    }
}