package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.CurrentTimeProvider;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.model.RequestTrace;
import de.effectivetrainings.observed.model.TraceType;
import de.effectivetrainings.observed.model.TracerContext;

public class OutboundRequestTracerRequestCycle extends AbstractTracerRequestCycle {
    public OutboundRequestTracerRequestCycle(TracerContextProvider tracerContextProvider, TraceCollector traceCollector, TraceApplicationInfo traceApplicationInfo, CurrentTimeProvider currentTimeProvider) {
        super(tracerContextProvider, traceCollector, traceApplicationInfo, currentTimeProvider);
    }

    @Override
    public TracerContext beforeRequest(TracerContext context) {
        //ensure source is set for any outbound request
        context
                .getTimer()
                .start();
        final RequestTrace requestTrace = trace(context, traceApplicationInfo.getName(), null, -1l, TraceType.REQUEST_OUTBOUND);
        traceCollector.collect(requestTrace);
        return context;
    }

    @Override
    public TracerContext afterRequest(TracerContext context) {
        context
                .getTimer()
                .stop();
        final RequestTrace responseRequestTrace = trace(context, traceApplicationInfo.getName(), context.getOrigSource(), context
                .getTimer()
                .duration(), TraceType.RESPONSE);
        traceCollector.collect(responseRequestTrace);
        return context;
    }
}
