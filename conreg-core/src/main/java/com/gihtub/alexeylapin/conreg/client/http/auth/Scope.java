package com.gihtub.alexeylapin.conreg.client.http.auth;

import com.gihtub.alexeylapin.conreg.image.Reference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Scope {

    String getResourceType();

    String getResourceName();

    Action getAction();

    static Scope of(String resourceType, String resourceName, Action action) {
        return new NamedScope(resourceType, resourceName, action);
    }

    static Scope repository(Reference reference, Action action) {
        return of("repository", reference.getNamespace() + "/" + reference.getName(), action);
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    @NonNull
    class NamedScope implements Scope {

        private final String resourceType;
        private final String resourceName;
        private final Action action;

    }

}
