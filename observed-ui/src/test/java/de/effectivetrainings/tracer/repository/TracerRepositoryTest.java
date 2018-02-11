package de.effectivetrainings.tracer.repository;

import de.effectivetrainings.tracer.domain.Span;
import de.effectivetrainings.tracer.domain.TimeSpan;
import de.effectivetrainings.tracer.domain.graph.InfluxQueryResult;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        final List<Span> spansWith2OrMoreTraces = spans.stream().filter(span -> span.getTraces().size() > 1).collect(Collectors.toList());

        final Optional<TimeSpan> timespan = testee.findTimespan();
        assertTrue(timespan.isPresent());

        final List<InfluxQueryResult> callDurations = testee.findCallDurations(new Date());
        assertNotNull(callDurations);

    }
}