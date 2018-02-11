package de.effectivetrainings.observed.spring.config;

import de.effectivetrainings.observed.CorrelationIdResolver;
import de.effectivetrainings.observed.CurrentTimeProvider;
import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.TracerContextHolder;
import de.effectivetrainings.observed.TracerRequestLifeCycle;
import de.effectivetrainings.observed.impl.DefaultCorrelationIdResolver;
import de.effectivetrainings.observed.impl.DefaultCurrentTimeProvider;
import de.effectivetrainings.observed.impl.InboundRequestTracerRequestCycle;
import de.effectivetrainings.observed.impl.OutboundRequestTracerRequestCycle;
import de.effectivetrainings.observed.impl.TracerContextProvider;
import de.effectivetrainings.observed.spring.TraceRestTemplateBeanPostProcessor;
import de.effectivetrainings.observed.spring.TracerClientHttpInterceptor;
import de.effectivetrainings.observed.spring.TracerMethodHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class TracerMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private TraceCollector traceCollector;
    @Autowired
    private TraceApplicationInfo traceApplicationInfo;
    @Autowired
    private TracerContextProvider tracerContextProvider;
    @Autowired
    private TracerContextHolder tracerContextHolder;

    @Bean
    public TracerRequestLifeCycle inboundRequestLifeCycle() {
        return new InboundRequestTracerRequestCycle(tracerContextProvider, traceCollector, traceApplicationInfo, currentTimeProvider());
    }

    @Bean
    public TracerRequestLifeCycle outboundRequestLifeCycle() {
        return new OutboundRequestTracerRequestCycle(tracerContextProvider, traceCollector, traceApplicationInfo, currentTimeProvider());
    }

    @ConditionalOnMissingBean(CorrelationIdResolver.class)
    @Bean
    public CorrelationIdResolver correlationIdResolver() {
        return new DefaultCorrelationIdResolver();
    }

    @ConditionalOnMissingBean(CurrentTimeProvider.class)
    @Bean
    public CurrentTimeProvider currentTimeProvider() {
        return new DefaultCurrentTimeProvider();
    }

    @Bean
    public TracerMethodHandlerAdapter tracerMethodHandlerAdapter() {
        return new TracerMethodHandlerAdapter(inboundRequestLifeCycle(), tracerContextProvider, tracerContextHolder);
    }

    @Bean
    public TracerClientHttpInterceptor tracerClientHttpInterceptor() {
        return new TracerClientHttpInterceptor(outboundRequestLifeCycle(), tracerContextProvider);
    }

    @Bean
    public TraceRestTemplateBeanPostProcessor traceRestTemplateBeanPostProcessor() {
        return new TraceRestTemplateBeanPostProcessor(tracerClientHttpInterceptor());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(tracerMethodHandlerAdapter());
    }
}
