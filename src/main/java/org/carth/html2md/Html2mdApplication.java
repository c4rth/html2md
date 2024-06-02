package org.carth.html2md;

import lombok.RequiredArgsConstructor;
import org.carth.html2md.command.ConvertCommand;
import org.carth.html2md.config.CommandLineArgs;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
@RequiredArgsConstructor
public class Html2mdApplication implements ApplicationRunner, ExitCodeGenerator {

    private final ConvertCommand convertCommand;
    private final CommandLine.IFactory factory;
    private final CommandLineArgs commandLineArgs;

    private int exitCode;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Html2mdApplication.class, args)));
    }

    @Override
    public void run(ApplicationArguments args) {
        CommandLine commandLine = new CommandLine(convertCommand, factory);
        commandLine.parseArgs(args.getSourceArgs());

        commandLineArgs.setPage(convertCommand.getPage());
        commandLineArgs.setDebug(convertCommand.isDebug());

        exitCode = commandLine.execute(args.getSourceArgs());
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
