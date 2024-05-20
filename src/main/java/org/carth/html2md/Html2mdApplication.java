package org.carth.html2md;

import org.carth.html2md.command.ConvertCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class Html2mdApplication implements CommandLineRunner, ExitCodeGenerator {

    private final ConvertCommand convertCommand;

    private final CommandLine.IFactory factory;

    private int exitCode;

    public Html2mdApplication(ConvertCommand convertCommand, CommandLine.IFactory factory) {
        this.convertCommand = convertCommand;
        this.factory = factory;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Html2mdApplication.class, args)));
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(convertCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
