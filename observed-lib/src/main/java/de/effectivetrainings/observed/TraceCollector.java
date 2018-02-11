package de.effectivetrainings.observed;


import de.effectivetrainings.observed.model.RequestTrace;

public interface TraceCollector {

    void collect(RequestTrace requestTrace);

}
