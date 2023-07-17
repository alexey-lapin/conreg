package com.gihtub.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.client.http.RegistryDescriptor;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WellKnownRegistries implements RegistryResolver {

    private final Map<String, RegistryDescriptor> descriptors;

    public WellKnownRegistries() {
        this(defaultRegistries());
    }

    public WellKnownRegistries(Map<String, RegistryDescriptor> descriptors) {
        this.descriptors = new ConcurrentHashMap<>(descriptors);
    }

    @Override
    public RegistryDescriptor resolve(String registry) {
        return descriptors.getOrDefault(registry, RegistryDescriptor.builder()
                .canonicalName(registry)
                .serviceName(registry)
                .serviceUri(URI.create("https://" + registry))
                .build());
    }

    public static Map<String, RegistryDescriptor> defaultRegistries() {
        Map<String, RegistryDescriptor> map = new HashMap<>();
        map.put("localhost:5000", RegistryDescriptor.builder()
                .canonicalName("localhost:5000")
                .serviceName("localhost:5000")
                .serviceUri(URI.create("http://localhost:5000"))
                .build());
        map.put("docker.io", RegistryDescriptor.builder()
                .canonicalName("docker.io")
                .serviceName("registry.docker.io")
                .serviceUri(URI.create("https://registry-1.docker.io"))
                .build());
        return map;
    }

}
