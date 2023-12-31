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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class TarInputStreamImageLoader implements ImageLoader {

    private final JsonCodec jsonCodec;
    private final InputStream inputStream;
    private final Path tmp;

    @SneakyThrows
    public TarInputStreamImageLoader(JsonCodec jsonCodec, InputStream inputStream, Path tmpDir) {
        this.jsonCodec = jsonCodec;
        this.inputStream = inputStream;
        this.tmp = Files.createDirectories(tmpDir);
    }

    @Override
    public Image load() throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(inputStream)) {
            Manifest manifest = null;
            Blob config = null;
            List<Blob> layers = new ArrayList<>();
            TarArchiveEntry entry = null;
            while ((entry = tis.getNextTarEntry()) != null) {
                if (entry.isDirectory() || entry.isSymbolicLink()) {
                    continue;
                }
                if (!tis.canReadEntryData(entry)) {
                    throw new IOException("tar entry is not readable");
                }
                String entryName = entry.getName();
                String fileName = Paths.get(entryName).getFileName().toString();

                int lastIndexOfDot = fileName.lastIndexOf(".");
                if (lastIndexOfDot > 0) {
                    String name = fileName.substring(0, lastIndexOfDot);
                    String ext = fileName.substring(lastIndexOfDot + 1);
                    Path itemPath = tmp.resolve(entryName);
                    Files.createDirectories(itemPath.getParent());
                    Files.copy(tis, itemPath);
                    if ("json".equalsIgnoreCase(ext)) {
                        if ("manifest".equalsIgnoreCase(name)) {
                            String manifestString = new String(Files.readAllBytes(itemPath), StandardCharsets.UTF_8);
                            List<Manifest> manifests = jsonCodec.decodeList(manifestString, Manifest.class);
                            if (manifests != null && !manifests.isEmpty()) {
                                manifest = manifests.get(0);
                            }
                        } else {
                            config = Blob.ofJson(digest(name), entry.getSize(), () -> Files.newInputStream(itemPath));
                        }
                    } else if ("tar".equalsIgnoreCase(ext)) {
                        layers.add(Blob.ofTar(digest(name), entry.getSize(), () -> Files.newInputStream(itemPath)));
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
        }
    }

    @Override
    public void close() throws Exception {
        try (Stream<Path> stream = Files.walk(tmp)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    private static String digest(String name) {
        return "sha256:" + name;
    }

}
