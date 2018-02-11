package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.CurrentTimeProvider;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.TracerRequestLifeCycle;
import de.effectivetrainings.observed.model.RequestTrace;
import de.effectivetrainings.observed.model.TraceRequestType;
import de.effectivetrainings.observed.model.TraceType;
import de.effectivetrainings.observed.model.TracerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
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
        final RequestTrace errorRequestTrace = trace(context, null, traceApplicationInfo.getName(), -1, TraceType.EXCEPTION);
        traceCollector.collect(errorRequestTrace);
        return context;
    }


    protected RequestTrace trace(TracerContext context, String source, String target, long duration, TraceType traceType) {
        final RequestTrace build = RequestTrace
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
        log.debug("Trace  {}", build);
        return build;
    }

    protected String id() {
        return UUID.randomUUID().toString();
    }



}
