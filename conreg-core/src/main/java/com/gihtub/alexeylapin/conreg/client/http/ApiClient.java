package com.gihtub.alexeylapin.conreg.client.http;

import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.image.Reference;

import java.io.InputStream;
import java.util.regex.Pattern;

public interface ApiClient {

    String BASE = "%s/v2/";
    String CATALOG = "%s/v2/_catalog?n=%s&last=%s";
    String TAGS = "%s/v2/%s/tags/list";
    String MANIFEST = "%s/v2/%s/%s/manifests/%s";
    String BLOB = "%s/v2/%s/%s/blobs/%s";
    String BLOB_UPLOAD = "%s/v2/%s/blobs/uploads/";

    Pattern AUTH_URL_PATTERN = Pattern.compile("Bearer realm=\"(.*?)\",service=\"(.*?)\"");

    ManifestDto getManifest(Reference reference);

    InputStream getBlob(Reference reference, String digest);

}
