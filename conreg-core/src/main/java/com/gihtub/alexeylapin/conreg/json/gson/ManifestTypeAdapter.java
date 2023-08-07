package com.gihtub.alexeylapin.conreg.json.gson;

import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.image.Manifest;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ManifestTypeAdapter extends TypeAdapter<Manifest> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public void write(JsonWriter out, Manifest src) throws IOException {
        out.beginObject();
        out.name("Config").value(src.getConfig());
        out.name("RepoTags").beginArray();
        List<String> repoTags = src.getRepoTags();
        if (repoTags != null && !repoTags.isEmpty()) {
            for (String repoTag : repoTags) {
                out.value(repoTag);
            }
        }
        out.endArray();
        out.name("Layers").beginArray();
        List<String> layers = src.getLayers();
        if (layers != null && !layers.isEmpty()) {
            for (String layer : layers) {
                out.value(layer);
            }
        }
        out.endArray();
        out.endObject();
    }

    @Override
    public Manifest read(JsonReader in) throws IOException {
        String config = null;
        List<String> repoTags = new ArrayList<>();
        List<String> layers = new ArrayList<>();

        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                continue;
            }

            switch (key) {
                case "Config":
                    config  = in.nextString();
                    break;
                case "RepoTags":
                    in.beginArray();
                    while (in.hasNext()) {
                        repoTags.add(in.nextString());
                    }
                    in.endArray();
                    break;
                case "Layers":
                    in.beginArray();
                    while (in.hasNext()) {
                        layers.add(in.nextString());
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return new Manifest(config, repoTags, layers);
    }

}
