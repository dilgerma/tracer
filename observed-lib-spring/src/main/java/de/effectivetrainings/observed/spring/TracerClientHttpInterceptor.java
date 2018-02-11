package de.effectivetrainings.observed.spring;

import de.effectivetrainings.observed.TracerRequestLifeCycle;
import de.effectivetrainings.observed.impl.Headers;
import de.effectivetrainings.observed.impl.TracerContextProvider;
import de.effectivetrainings.observed.model.TracerContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class TracerClientHttpInterceptor implements ClientHttpRequestInterceptor {

    @NonNull
    private TracerRequestLifeCycle tracerRequestLifeCycle;
    @NonNull
    private TracerContextProvider tracerContextProvider;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        final TracerContext context = tracerContextProvider.provide(Headers.of(httpRequest
                .getHeaders()
                .toSingleValueMap()));
        tracerRequestLifeCycle.beforeRequest(context);
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        tracerRequestLifeCycle.afterRequest(context);
        return response;
    }
}
