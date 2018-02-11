package de.effectivetrainings.observed.impl;

import de.effectivetrainings.observed.HostProvider;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class DefaultHostProvider implements HostProvider {

    private final String hostname;

    public DefaultHostProvider() {
        hostname = determineHostName();
    }

    private String determineHostName() {
        try {
            return Inet4Address
                    .getLocalHost()
                    .getHostName();
        } catch (UnknownHostException e) {
            return "undefined_host";
        }
    }

    @Override
    public String provideHostName() {
        return hostname;
    }
}
