package com.github.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.facade.RegistryClient;
import com.gihtub.alexeylapin.conreg.facade.RegistryClients;
import org.junit.jupiter.api.Test;

public class FacadeTest {

    @Test
    void name() {
        RegistryClient client = RegistryClients.defaultClient();
    }

}
