package com.gihtub.alexeylapin.conreg.client.http;

import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.model.Reference;

public interface ApiClient {

    ManifestDto getManifest(Reference reference);

}
