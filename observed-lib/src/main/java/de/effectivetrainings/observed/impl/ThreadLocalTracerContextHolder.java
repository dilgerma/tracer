package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.TracerContextHolder;
import de.effectivetrainings.observed.model.TracerContext;

import java.util.Optional;

public class ThreadLocalTracerContextHolder implements TracerContextHolder {

    private ThreadLocal<TracerContext> tracerContextThreadLocal = new ThreadLocal<>();

    @Override
    public void set(TracerContext context) {
        tracerContextThreadLocal.set(context);
    }

    @Override
    public Optional<TracerContext> get() {
        return Optional.ofNullable(tracerContextThreadLocal.get());
    }

    @Override
    public void clear() {
        tracerContextThreadLocal.remove();
    }
}
