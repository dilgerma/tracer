package de.effectivetrainings.observed.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.effectivetrainings.observed.impl.TracerContextProvider;
import lombok.Getter;

@Getter
public class TracerContext {

    private String source;
    private String correlationId;
    private String reporter;
    private int hops;
    private Multimap<String, String> headers = HashMultimap.create();

    private Timer timer = new Timer();

    public TracerContext(String source, String reporter, String correlationId, int hops, Multimap<String, String> headers) {
        this.source = source;
        this.correlationId = correlationId;
        this.reporter = reporter;
        this.hops = hops;
        this.headers = HashMultimap.create(headers);
    }

    public TracerContext withHeader(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

    public TracerContext forSource(String source) {
        this.headers.put(TracerContextProvider.X_SOURCE, source);
        return this;
    }
}
