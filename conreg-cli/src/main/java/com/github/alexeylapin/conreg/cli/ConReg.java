package com.github.alexeylapin.conreg.cli;

import com.github.alexeylapin.conreg.cli.command.LoadCommand;
import com.github.alexeylapin.conreg.cli.command.SaveCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "conreg",
        subcommands = {
                SaveCommand.class,
                LoadCommand.class
        }
)
public class ConReg {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ConReg()).execute(args);
        System.exit(exitCode);
    }

}
