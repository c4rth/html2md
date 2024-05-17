package org.carth.html2md;

import org.carth.html2md.command.ConvertCommand;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class Html2MdApplicationRunner implements CommandLineRunner, ExitCodeGenerator {

	private final ConvertCommand convertCommand;

	private final IFactory factory;

	private int exitCode;

	public Html2MdApplicationRunner(ConvertCommand convertCommand, IFactory factory) {
		this.convertCommand = convertCommand;
		this.factory = factory;
	}

	@Override
	public void run(String... args) throws Exception {
		exitCode = new CommandLine(convertCommand, factory).execute(args);
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}
}