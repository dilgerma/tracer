package de.effectivetrainings.observed.influx;

import org.springframework.scheduling.annotation.Scheduled;

public class InfluxTraceScheduler {

    private InfluxTraceCollector traceReporter;

    public InfluxTraceScheduler(InfluxTraceCollector traceReporter) {
        this.traceReporter = traceReporter;
    }

    @Scheduled(fixedRateString = "${tracer.influx.schedule:60000}")
    public void schedule() {
        traceReporter.report();
    }
}
