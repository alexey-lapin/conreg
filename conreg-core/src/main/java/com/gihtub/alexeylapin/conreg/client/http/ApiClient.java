package com.gihtub.alexeylapin.conreg.client.http;

import com.gihtub.alexeylapin.conreg.client.http.auth.TokenKey;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDescriptor;
import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Reference;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;

public interface ApiClient {

    String SCHEMA_HTTP = "http";

    String URL_TOKEN = "%s?service=%s&scope=%s";
    String URL_BASE = "%s/v2/";
    String URL_CATALOG = "%s/v2/_catalog?n=%s&last=%s";
    String URL_TAGS = "%s/v2/%s/tags/list";
    String URL_MANIFEST = "%s/v2/%s/%s/manifests/%s";
    String URL_BLOB = "%s/v2/%s/%s/blobs/%s";
    String URL_BLOB_UPLOAD = "%s/v2/%s/%s/blobs/uploads/";

    Pattern AUTH_CHALLENGE_PATTERN = Pattern.compile("Bearer realm=\"(.*?)\"(,service=\"(.*?)\")?(,scope=\"(.*?)\")?");
    Pattern SCOPE_PATTERN = Pattern.compile("(.*):(.*):(.*)");

    Optional<TokenDto> authenticate(RegistryDescriptor registryDescriptor, String challenge, TokenKey tokenKey);

    ManifestDescriptor getManifest(Reference reference);

    void deleteManifest(Reference reference);

    void putManifest(Reference reference, ManifestDescriptor manifest);

    URI startPush(Reference reference);

    void cancelPush(Reference reference, URI uri);

    boolean isBlobExists(Reference reference, String digest);

    InputStream getBlob(Reference reference, String digest);

    void putBlob(Reference reference, URI uploadUri, String digest, Blob blob);

}
