package com.gihtub.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WellKnownRegistries implements RegistryResolver {

    private final Map<String, String> registries;

    public WellKnownRegistries() {
        this(defaultRegistries());
    }

    public WellKnownRegistries(Map<String, String> registries) {
        this.registries = new ConcurrentHashMap<>(registries);
    }

    @Override
    public String resolve(String registry) {
        return registries.getOrDefault(registry, "https://" + registry);
    }

    public static Map<String, String> defaultRegistries() {
        Map<String, String> map = new HashMap<>();
        map.put("localhost:5000", "http://localhost:5000");
        map.put("docker.io", "https://registry-1.docker.io");
        return map;
    }

}
