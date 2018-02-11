package de.effectivetrainings.observed.model;

public enum TraceRequestType {
    REST("rest");

    private String traceRequestType;

    TraceRequestType(String traceRequestType) {
        this.traceRequestType = traceRequestType;
    }

    public String getTraceRequestType() {
        return traceRequestType;
    }
}
