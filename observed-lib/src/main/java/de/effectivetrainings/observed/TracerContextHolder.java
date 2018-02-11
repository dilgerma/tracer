package de.effectivetrainings.observed;

import de.effectivetrainings.observed.model.TracerContext;

import java.util.Optional;

public interface TracerContextHolder {

    void set(TracerContext context);

    Optional<TracerContext> get();

    void clear();
}
