package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.CurrentTimeProvider;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.model.RequestTrace;
import de.effectivetrainings.observed.model.TraceType;
import de.effectivetrainings.observed.model.TracerContext;

public class InboundRequestTracerRequestCycle extends AbstractTracerRequestCycle {
    public InboundRequestTracerRequestCycle(TracerContextProvider tracerContextProvider, TraceCollector traceReporter, TraceApplicationInfo traceApplicationInfo, CurrentTimeProvider currentTimeProvider) {
        super(tracerContextProvider, traceReporter, traceApplicationInfo, currentTimeProvider);
    }

    @Override
    public TracerContext beforeRequest(TracerContext context) {
        context
                .getTimer()
                .start();
        final RequestTrace requestTrace = trace(context, context.getOrigSource(), traceApplicationInfo.getName(), -1l, TraceType.REQUEST_INBOUND);
        traceCollector.collect(requestTrace);
        return context;
    }

    @Override
    public TracerContext afterRequest(TracerContext context) {
        context
                .getTimer()
                .stop();
        final RequestTrace responseRequestTrace = trace(context, traceApplicationInfo.getName(), null, context
                .getTimer()
                .duration(), TraceType.RESPONSE);
        traceCollector.collect(responseRequestTrace);
        return context;
    }

}
