package de.effectivetrainings.observed;


import de.effectivetrainings.observed.impl.Headers;

public interface CorrelationIdResolver {

    String resolve(Headers headers);
}
