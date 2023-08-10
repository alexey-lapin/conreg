package com.github.alexeylapin.conreg.cli.command;

import com.gihtub.alexeylapin.conreg.facade.RegistryClient;
import com.gihtub.alexeylapin.conreg.facade.RegistryClients;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJsonCodecFactory;
import com.github.alexeylapin.conreg.facade.JdkApiClientBuilder;
import com.github.alexeylapin.conreg.http.JdkApiClient;
import com.github.alexeylapin.conreg.http.LoggingHttpClient;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;

public abstract class AbstractCommand {

    @CommandLine.ArgGroup(exclusive = false)
    protected ProxyOptions proxyOptions;

    protected static class ProxyOptions {

        @CommandLine.Option(names = {"--proxy-host"}, required = true)
        protected String proxyHost;

        @CommandLine.Option(names = {"--proxy-port"}, required = true)
        protected String proxyPort;

    }

    protected RegistryClient createRegistryClient() {
        RegistryClient client;
        if (proxyOptions == null) {
            client = RegistryClients.defaultClient();
        } else {
            JsonCodec jsonCodec = new JacksonJsonCodecFactory().create()
                    .orElseThrow(() -> new RuntimeException("json codec not found"));
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .proxy(ProxySelector.of(new InetSocketAddress("localhost", 8888)))
                    .build();
            JdkApiClient apiClient = new JdkApiClientBuilder()
                    .httpClient(new LoggingHttpClient(httpClient))
                    .jsonCodec(jsonCodec)
                    .build();
            client = RegistryClients.builder()
                    .apiClient(apiClient)
                    .jsonCodec(jsonCodec)
                    .build();
        }
        return client;
    }

}
