package de.effectivetrainings.observed.model;

import lombok.Getter;

@Getter
public enum TraceType {
    REQUEST_OUTBOUND("request_outbound"), REQUEST_INBOUND("request_inbound"), RESPONSE("response"), EXCEPTION("exception");

    private String type;

    TraceType(String type) {
        this.type = type;
    }
}
