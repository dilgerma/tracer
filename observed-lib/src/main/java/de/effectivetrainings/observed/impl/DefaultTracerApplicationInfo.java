package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.TraceApplicationInfo;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DefaultTracerApplicationInfo implements TraceApplicationInfo {

    @NonNull
    private String name;
    @NonNull
    private String environment;
    @NonNull
    private String host;
}
