package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.json.Json;
import com.gihtub.alexeylapin.conreg.image.Image;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DefaultFileOperations implements FileOperations {

    private final Json json;

    public DefaultFileOperations(Json json) {
        this.json = json;
    }

    public void save(Image image, OutputStream outputStream) {
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(outputStream)) {
            TarArchiveEntry manifestEntry = new TarArchiveEntry(MANIFEST);
            byte[] manifestBytes = json.render(image.getManifest()).getBytes(StandardCharsets.UTF_8);
            manifestEntry.setSize(manifestBytes.length);
            tos.putArchiveEntry(manifestEntry);
            tos.write(manifestBytes);
            tos.closeArchiveEntry();
            for (Blob blob : image.getBlobs()) {
                TarArchiveEntry blobEntry = new TarArchiveEntry(blob.getName());
                blobEntry.setSize(blob.getSize());
                tos.putArchiveEntry(blobEntry);
                try (InputStream is = blob.getContent().get()) {
                    IOUtils.copy(is, tos);
                }
                tos.closeArchiveEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
