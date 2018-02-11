package de.effectivetrainings.observed.impl;


import de.effectivetrainings.observed.CurrentTimeProvider;

public class DefaultCurrentTimeProvider implements CurrentTimeProvider {
    @Override
    public long currentTimeInMillis() {
        return System.currentTimeMillis();

    }
}
