package de.effectivetrainings.observed.spring.config;

import de.effectivetrainings.observed.CorrelationIdResolver;
import de.effectivetrainings.observed.HostProvider;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.impl.DefaultHostProvider;
import de.effectivetrainings.observed.impl.DefaultTracerApplicationInfo;
import de.effectivetrainings.observed.impl.TracerContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracerConfig {

    @Autowired
    private CorrelationIdResolver correlationIdResolver;

    @Value("${tracer.application.environment:undefined}")
    private String environment;
    @Value("${tracer.application.name:${spring.application.name:undefined}}")
    private String applicationName;

    @Bean
    public TraceApplicationInfo traceApplicationInfo() {
        return new DefaultTracerApplicationInfo(applicationName, environment, hostProvider().provideHostName());
    }

    @Bean
    public TracerContextProvider tracerContextProvider() {
        return new TracerContextProvider(correlationIdResolver, traceApplicationInfo());
    }

    @Bean
    public HostProvider hostProvider() {
        return new DefaultHostProvider();
    }
}
