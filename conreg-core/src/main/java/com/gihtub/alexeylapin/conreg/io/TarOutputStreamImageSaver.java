package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RequiredArgsConstructor
public class TarOutputStreamImageSaver implements ImageSaver {

    private final JsonCodec jsonCodec;
    private final OutputStream outputStream;

    @Override
    public void save(Image image) throws IOException {
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(outputStream)) {
            TarArchiveEntry manifestEntry = new TarArchiveEntry(MANIFEST);
            byte[] manifestBytes = jsonCodec.encode(Collections.singletonList(image.getManifest())).getBytes(StandardCharsets.UTF_8);
            manifestEntry.setSize(manifestBytes.length);
            tos.putArchiveEntry(manifestEntry);
            tos.write(manifestBytes);
            tos.closeArchiveEntry();
            for (Blob blob : image.getBlobs()) {
                TarArchiveEntry blobEntry = new TarArchiveEntry(blob.getName());
                blobEntry.setSize(blob.getSize());
                tos.putArchiveEntry(blobEntry);
                try (InputStream is = blob.getContent().get()) {
                    FileOperations.copy(is, tos);
                }
                tos.closeArchiveEntry();
            }
        }
    }

    @Override
    public void close() throws Exception {

    }

}
