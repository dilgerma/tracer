package de.effectivetrainings.tracer.repository;

import de.effectivetrainings.tracer.domain.Span;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TracerRepositoryTest {

    private TracerRepository testee;

    @Before
    public void setUp() {
        InfluxDB influxDB = new InfluxDBImpl("http://localhost:8086", null, null, new OkHttpClient.Builder());
        testee = new TracerRepository(influxDB, "tracer");
    }

    @Test
    @DisplayName("Queries InfluxDB")
    public void testQueryInflux() {
        final List<Span> spans = testee.findSpans();
        assertFalse(spans.isEmpty());
    }
}