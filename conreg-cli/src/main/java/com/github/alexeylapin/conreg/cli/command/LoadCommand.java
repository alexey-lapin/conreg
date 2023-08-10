package com.github.alexeylapin.conreg.cli.command;

import com.gihtub.alexeylapin.conreg.facade.RegistryClient;
import com.gihtub.alexeylapin.conreg.facade.RegistryClients;
import com.gihtub.alexeylapin.conreg.image.Reference;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

@Command(name = "load")
public class LoadCommand extends AbstractCommand implements Runnable {

    @Parameters(arity = "1")
    private String image;

    @Option(names = {"-i", "--input"}, required = true)
    private Path input;

    @Override
    public void run() {
        RegistryClient registryClient = createRegistryClient();
        registryClient.push(input, Reference.of(image));
    }

}
