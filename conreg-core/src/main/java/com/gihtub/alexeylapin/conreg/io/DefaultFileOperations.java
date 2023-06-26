package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Manifest;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.compress.utils.IOUtils.copy;

public class DefaultFileOperations implements FileOperations {

    private final JsonCodec jsonCodec;

    public DefaultFileOperations(JsonCodec jsonCodec) {
        this.jsonCodec = jsonCodec;
    }

    @Override
    public void save(Image image, OutputStream outputStream) {
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(outputStream)) {
            TarArchiveEntry manifestEntry = new TarArchiveEntry(MANIFEST);
            byte[] manifestBytes = jsonCodec.encode(image.getManifest()).getBytes(StandardCharsets.UTF_8);
            manifestEntry.setSize(manifestBytes.length);
            tos.putArchiveEntry(manifestEntry);
            tos.write(manifestBytes);
            tos.closeArchiveEntry();
            for (Blob blob : image.getBlobs()) {
                TarArchiveEntry blobEntry = new TarArchiveEntry(blob.getName());
                blobEntry.setSize(blob.getSize());
                tos.putArchiveEntry(blobEntry);
                try (InputStream is = blob.getContent().get()) {
                    copy(is, tos);
                }
                tos.closeArchiveEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Image load(InputStream inputStream) {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(inputStream)) {
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

//                Path itemPath = dst.resolve(name);
                if (parts.length == 2) {
                    if ("json".equalsIgnoreCase(parts[1])) {
                        if ("manifest".equalsIgnoreCase(parts[0])) {
                            byte[] bytes = new byte[(int) entry.getSize()];
                            IOUtils.readFully(tis, bytes);
                            String manifestString = new String(bytes, StandardCharsets.UTF_8);
                            manifest = jsonCodec.decode(manifestString, Manifest.class);
                        } else {
                            config = Blob.ofJson(parts[0], entry.getSize(), () -> null);
                        }
                    } else {
                        layers.add(Blob.ofTar(parts[0], entry.getSize(), () -> null));
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

}
