package com.github.alexeylapin.conreg.http;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
@Slf4j
public class LoggingHttpClient extends HttpClient {

    @NonNull
    @Delegate(excludes = DelegateExclusion.class)
    private final HttpClient delegate;

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException {
        log.debug(">> {} {}", request.method(), request.uri());
        HttpResponse<T> response = delegate.send(request, responseBodyHandler);
        log.debug("<< {} {}", response.statusCode(), response.uri());
        return response;
    }

    private interface DelegateExclusion {
        <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
                throws IOException, InterruptedException;
    }

}
