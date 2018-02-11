
package de.effectivetrainings.observed.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class TraceRestTemplateBeanPostProcessor implements BeanPostProcessor {

    private TracerClientHttpInterceptor tracerClientHttpInterceptor;

    public TraceRestTemplateBeanPostProcessor(TracerClientHttpInterceptor tracerClientHttpInterceptor) {
        this.tracerClientHttpInterceptor = tracerClientHttpInterceptor;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            ((RestTemplate) bean)
                    .getInterceptors()
                    .add(tracerClientHttpInterceptor);
        }
        if (bean instanceof RestTemplateBuilder) {
            ((RestTemplateBuilder) bean).additionalInterceptors(tracerClientHttpInterceptor);
        }
        return bean;
    }
}