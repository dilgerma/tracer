package de.effectivetrainings.observed.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;

@Getter
public class TracerContext {

    private String origSource;
    private String correlationId;
    private String reporter;
    private int hops;
    private Multimap<String, String> headers = HashMultimap.create();

    private Timer timer = new Timer();

    public TracerContext(String origSource, String reporter, String correlationId, int hops, Multimap<String, String> headers) {
        this.origSource = origSource;
        this.correlationId = correlationId;
        this.reporter = reporter;
        this.hops = hops;
        this.headers = HashMultimap.create(headers);
    }

    public TracerContext withHeader(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

}
