package com.github.alexeylapin.conreg.http;


import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.model.Reference;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

class JdkApiClientTest {

    @Test
    void name() {
        HttpClient httpClient = HttpClient.newHttpClient();
        ApiClient apiClient = new JdkApiClient(httpClient, new Auth(httpClient, null);
        ManifestDto manifest = apiClient.getManifest(new Reference("https://registry-1.docker.io",
                "library/alpine", "latest", null));

    }

}