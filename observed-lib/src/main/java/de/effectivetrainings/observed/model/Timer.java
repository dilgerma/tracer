package de.effectivetrainings.observed.model;

public class Timer {

    private long start;
    private long duration;

    public Timer() {
    }

    public Timer start() {
        start = System.currentTimeMillis();
        return this;
    }

    public Timer stop() {
        duration = System.currentTimeMillis() - start;
        return this;
    }

    public long duration() {
        return duration;
    }

}
