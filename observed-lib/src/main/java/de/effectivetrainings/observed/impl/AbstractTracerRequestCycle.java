package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.CurrentTimeProvider;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.TracerRequestLifeCycle;
import de.effectivetrainings.observed.model.Trace;
import de.effectivetrainings.observed.model.TraceRequestType;
import de.effectivetrainings.observed.model.TraceType;
import de.effectivetrainings.observed.model.TracerContext;

import java.util.UUID;

public abstract class AbstractTracerRequestCycle implements TracerRequestLifeCycle {

    protected TracerContextProvider tracerContextProvider;
    protected TraceCollector traceCollector;
    protected TraceApplicationInfo traceApplicationInfo;
    protected CurrentTimeProvider currentTimeProvider;

    public AbstractTracerRequestCycle(TracerContextProvider tracerContextProvider, TraceCollector traceCollector, TraceApplicationInfo traceApplicationInfo, CurrentTimeProvider currentTimeProvider) {
        this.tracerContextProvider = tracerContextProvider;
        this.traceCollector = traceCollector;
        this.traceApplicationInfo = traceApplicationInfo;
        this.currentTimeProvider = currentTimeProvider;
    }


    @Override
    public abstract TracerContext beforeRequest(TracerContext tracerContext);

    @Override
    public abstract TracerContext afterRequest(TracerContext context);

    @Override
    public TracerContext onError(TracerContext context) {
        final Trace errorTrace = trace(context, null, traceApplicationInfo.getName(), -1, TraceType.EXCEPTION);
        traceCollector.collect(errorTrace);
        return context;
    }


    protected Trace trace(TracerContext context, String source, String target, long duration, TraceType traceType) {
        return Trace
                    .builder()
                    .reporter(traceApplicationInfo.getName())
                    .traceId(context.getCorrelationId())
                    .source(source)
                .target(target)
                    .environment(traceApplicationInfo.getEnvironment())
                    .host(traceApplicationInfo.getHost())
                    .timeStamp(currentTimeProvider.currentTimeInMillis())
                    .hops(context.getHops())
                    .duration(duration)
                .traceType(traceType)
                .traceRequestType(TraceRequestType.REST)
                    .build();
    }

    protected String id() {
        return UUID.randomUUID().toString();
    }



}
