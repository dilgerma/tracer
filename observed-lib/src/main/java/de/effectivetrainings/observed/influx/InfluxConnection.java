package de.effectivetrainings.observed.influx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "tracer.influx.connection")
public class InfluxConnection {

    @NonNull
    private String url;
    @NonNull
    private String user;
    @NonNull
    private String password;
    @NonNull
    private String database;
}
