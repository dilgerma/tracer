package de.effectivetrainings.observed.spring;

import de.effectivetrainings.observed.TraceReporter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class TraceScheduler {

    @Autowired
    @NonNull
    private TraceReporter traceReporter;

    @Scheduled(fixedRateString = "${tracer.influx.report.interval:10000}")
    public void schedule() {
        traceReporter.report();
    }
}
