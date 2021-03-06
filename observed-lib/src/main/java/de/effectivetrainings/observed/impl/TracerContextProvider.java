package de.effectivetrainings.observed.impl;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.effectivetrainings.observed.CorrelationIdResolver;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.model.TracerContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TracerContextProvider {

    public static final String X_ORIG_SOURCE = "X-TRACER-ORIG-SRC";
    public static final String X_CORRELATION_ID = "X-TRACER-CORRELATION-ID";
    public static final String X_HOST = "X-TRACER-HOSTNAME";
    public static final String X_TRACER_HOPS = "X-TRACER-HOPS";

    @NonNull
    private CorrelationIdResolver correlationIdResolver;
    @NonNull
    private TraceApplicationInfo traceApplicationInfo;

    public TracerContext provide(Headers headers) {
        String correlationId = correlationIdResolver.resolve(headers);
        int hops = parseHops(headers);

        final String origSource = headers.get(X_ORIG_SOURCE);
        Multimap<String, String> headerMap = HashMultimap.create();
        headerMap.put(X_CORRELATION_ID, correlationId);
        headerMap.put(X_TRACER_HOPS, String.valueOf(++hops));
        headerMap.put(X_HOST, traceApplicationInfo.getHost());
        headerMap.put(X_ORIG_SOURCE, traceApplicationInfo.getName());

        return new TracerContext(origSource, traceApplicationInfo.getName(), correlationId, hops, headerMap);
    }

    private int parseHops(Headers headers) {
        try {
            return Integer.parseInt(headers.getOrDefault(X_TRACER_HOPS, "0"));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

}
