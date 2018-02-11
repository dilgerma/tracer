package de.effectivetrainings.observed;


import de.effectivetrainings.observed.model.TracerContext;

public interface TracerRequestLifeCycle {

    TracerContext beforeRequest(TracerContext tracerContext);

    TracerContext afterRequest(TracerContext tracerContext);

    TracerContext onError(TracerContext tracerContext);
}
