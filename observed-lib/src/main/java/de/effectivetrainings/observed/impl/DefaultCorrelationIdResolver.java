package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.CorrelationIdResolver;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class DefaultCorrelationIdResolver implements CorrelationIdResolver {

    @Override
    public String resolve(Headers headers) {
        final String correlationId = headers.get(TracerContextProvider.X_CORRELATION_ID);
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }

}
