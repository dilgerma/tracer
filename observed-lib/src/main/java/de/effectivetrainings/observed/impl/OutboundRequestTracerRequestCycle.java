package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.CurrentTimeProvider;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.model.Trace;
import de.effectivetrainings.observed.model.TraceType;
import de.effectivetrainings.observed.model.TracerContext;

public class OutboundRequestTracerRequestCycle extends AbstractTracerRequestCycle {
    public OutboundRequestTracerRequestCycle(TracerContextProvider tracerContextProvider, TraceCollector traceCollector, TraceApplicationInfo traceApplicationInfo, CurrentTimeProvider currentTimeProvider) {
        super(tracerContextProvider, traceCollector, traceApplicationInfo, currentTimeProvider);
    }

    @Override
    public TracerContext beforeRequest(TracerContext context) {
        //ensure source is set for any outbound request
        TracerContext currentContext = context.forSource(traceApplicationInfo.getName());
        currentContext
                .getTimer()
                .start();
        final Trace trace = trace(currentContext, traceApplicationInfo.getName(), null, -1l, TraceType.REQUEST_OUTBOUND);
        traceCollector.collect(trace);
        return currentContext;
    }

    @Override
    public TracerContext afterRequest(TracerContext context) {
        context
                .getTimer()
                .stop();
        final Trace responseTrace = trace(context, traceApplicationInfo.getName(), context.getSource(), context
                .getTimer()
                .duration(), TraceType.RESPONSE);
        traceCollector.collect(responseTrace);
        return context;
    }
}
