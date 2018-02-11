package de.effectivetrainings.observed.spring.config;

import de.effectivetrainings.observed.FieldProvider;
import de.effectivetrainings.observed.TagProvider;
import de.effectivetrainings.observed.influx.InfluxConnection;
import de.effectivetrainings.observed.influx.InfluxTraceCollector;
import de.effectivetrainings.observed.spring.TraceScheduler;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@Configuration
@EnableConfigurationProperties(InfluxConnection.class)
@ConditionalOnProperty(value = "tracer.influx.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class TracerInfluxConfig {

    @Autowired
    private InfluxConnection influxConnection;
    @Autowired(required = false)
    private List<FieldProvider> fieldProviders;
    @Autowired(required = false)
    private List<TagProvider> tagProviders;

    @Bean
    public InfluxTraceCollector traceCollector() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return new InfluxTraceCollector(influxConnection, builder, fieldProviders, tagProviders);
    }

    @Bean
    public TraceScheduler traceScheduler() {
        return new TraceScheduler(traceCollector());
    }

}
