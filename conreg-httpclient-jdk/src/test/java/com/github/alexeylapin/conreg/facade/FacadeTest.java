package com.github.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.facade.RegistryClient;
import com.gihtub.alexeylapin.conreg.facade.RegistryClients;
import com.gihtub.alexeylapin.conreg.image.Reference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

@Disabled
public class FacadeTest {

    @Test
    void name() {
        RegistryClient client = RegistryClients.defaultClient();
        client.pull(Reference.of("alpine"), Path.of("alpine.tar"));
    }

}
