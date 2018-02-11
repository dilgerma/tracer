package de.effectivetrainings.observed.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Trace {
    protected String id;
    protected String traceId;
    protected String source;
    private String target;
    private String environment;
    private String reporter;
    private Long timeStamp;
    private String host;
    private TraceType traceType;
    private int hops = 0;
    private long duration = -1;
    private TraceRequestType traceRequestType;

}
