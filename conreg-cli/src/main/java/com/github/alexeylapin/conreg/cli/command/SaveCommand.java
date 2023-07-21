package com.github.alexeylapin.conreg.cli.command;

import com.gihtub.alexeylapin.conreg.facade.RegistryClient;
import com.gihtub.alexeylapin.conreg.facade.RegistryClients;
import com.gihtub.alexeylapin.conreg.image.Reference;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

@Command(name = "save")
public class SaveCommand extends AbstractCommand implements Runnable  {

    @Parameters(arity = "1")
    private String image;

    @Option(names = {"-o", "--output"}, required = true)
    private Path output;

    @Override
    public void run() {
        RegistryClient client = RegistryClients.defaultClient();
        client.pull(Reference.of(image), output);
    }

}
