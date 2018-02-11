package de.effectivetrainings.observed.lib.starter;

import de.effectivetrainings.observed.spring.config.TracerConfig;
import de.effectivetrainings.observed.spring.config.TracerInfluxConfig;
import de.effectivetrainings.observed.spring.config.TracerMvcConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({
        TracerConfig.class,
        TracerInfluxConfig.class,
        TracerMvcConfig.class
})
@PropertySource("classpath:default-influx-configs.properties")
public class TracerAutoConfiguration {
}

