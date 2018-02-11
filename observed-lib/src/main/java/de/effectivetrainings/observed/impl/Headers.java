package de.effectivetrainings.observed.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Wrapper that provides case insensitivie header semantics (as required by the spec)
 */
public class Headers {

    private Map<String, String> headers;

    private Headers(HttpServletRequest servletRequest) {
        this.headers = RequestHeaderUtils.headers(servletRequest);
    }

    private Headers(HttpServletResponse servletResponse) {
        this.headers = RequestHeaderUtils.headers(servletResponse);
    }

    private Headers(Map<String, String> headers) {
        Map<String, String> caseInsensitiveHeadersCopy = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            caseInsensitiveHeadersCopy.put(entry
                    .getKey()
                    .toLowerCase(), entry.getValue());
        }
        this.headers = caseInsensitiveHeadersCopy;
    }

    public static Headers of(HttpServletRequest servletRequest) {
        return new Headers(servletRequest);
    }

    public static Headers of(HttpServletResponse servletResponse) {
        return new Headers(servletResponse);
    }

    public static Headers of(Map<String, String> headers) {
        return new Headers(headers);
    }

    public String get(String key) {
        return headers.get(key.toLowerCase());
    }

    public String getOrDefault(String key, String defaultValue) {
        return headers.get(key.toLowerCase()) != null ? headers.get(key.toLowerCase()) : defaultValue;
    }

    public void put(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

    private static class RequestHeaderUtils {

        public static Map<String, String> headers(HttpServletRequest servletRequest) {

            Map<String, String> headers = new HashMap<>();

            final Enumeration<String> headerNames = servletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = servletRequest.getHeader(name);
                headers.put(name.toLowerCase(), value);
            }

            return headers;
        }

        public static Map<String, String> headers(HttpServletResponse servletResponse) {

            Map<String, String> headers = new HashMap<>();

            for (String name : servletResponse.getHeaderNames()) {
                String value = servletResponse.getHeader(name);
                headers.put(name.toLowerCase(), value);
            }

            return headers;
        }
    }
}
