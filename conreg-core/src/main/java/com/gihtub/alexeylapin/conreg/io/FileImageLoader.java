package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Manifest;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class FileImageLoader implements ImageLoader {

    private final JsonCodec jsonCodec;
    private final Path path;
    private final Path dst;

    @SneakyThrows
    public FileImageLoader(JsonCodec jsonCodec, Path path, Path dst) {
        this.jsonCodec = jsonCodec;
        this.path = path;
        this.dst = dst;
        Files.createDirectories(dst);
    }

    @Override
    public Image load() {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(Files.newInputStream(path))) {
            Manifest manifest = null;
            Blob config = null;
            List<Blob> layers = new ArrayList<>();
            TarArchiveEntry entry = null;
            while ((entry = tis.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                if (!tis.canReadEntryData(entry)) {
                    throw new IOException("read tar entry error");
                }
                String name = entry.getName();
                String[] parts = name.split("\\.");

                Path itemPath = dst.resolve(name);
                if (parts.length == 2) {
                    Files.copy(tis, itemPath);
                    if ("json".equalsIgnoreCase(parts[1])) {
                        if ("manifest".equalsIgnoreCase(parts[0])) {
                            String manifestString = new String(Files.readAllBytes(itemPath), StandardCharsets.UTF_8);
                            manifest = jsonCodec.decode(manifestString, Manifest.class);
                        } else {
                            config = Blob.ofJson(digest(parts[0]), entry.getSize(), () -> Files.newInputStream(itemPath));
                        }
                    } else {
                        layers.add(Blob.ofTar(digest(parts[0]), entry.getSize(), () -> Files.newInputStream(itemPath)));
                    }
                }
            }
            Reference reference = null;
            if (manifest != null) {
                List<String> repoTags = manifest.getRepoTags();
                if (repoTags != null && !repoTags.isEmpty()) {
                    reference = Reference.of(repoTags.get(0));
                }
            }
            return new Image(reference, config, layers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        try (Stream<Path> stream = Files.walk(dst)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    private static String digest(String name) {
        return "sha256:" + name;
    }

}
