package com.gihtub.alexeylapin.conreg.client.http;

import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Reference;

import java.io.InputStream;
import java.util.regex.Pattern;

public interface ApiClient {

    String URL_TOKEN = "%s?service=%s&scope=%s";
    String URL_BASE = "%s/v2/";
    String URL_CATALOG = "%s/v2/_catalog?n=%s&last=%s";
    String URL_TAGS = "%s/v2/%s/tags/list";
    String URL_MANIFEST = "%s/v2/%s/%s/manifests/%s";
    String URL_BLOB = "%s/v2/%s/%s/blobs/%s";
    String URL_BLOB_UPLOAD = "%s/v2/%s/%s/blobs/uploads/";

    Pattern AUTH_CHALLENGE_PATTERN = Pattern.compile("Bearer realm=\"(.*?)\",service=\"(.*?)\",scope=\"(.*?)\"");

    String authenticate(String registry, String challenge);

    ManifestDto getManifest(Reference reference);

    InputStream getBlob(Reference reference, String digest);

    void putBlob(Reference reference, String digest, Blob blob);

}
