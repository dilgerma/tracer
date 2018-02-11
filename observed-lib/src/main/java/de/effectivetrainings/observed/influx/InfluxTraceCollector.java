package de.effectivetrainings.observed.influx;

import de.effectivetrainings.observed.FieldProvider;
import de.effectivetrainings.observed.TagProvider;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.TraceReporter;
import de.effectivetrainings.observed.model.Trace;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.impl.InfluxDBImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class InfluxTraceCollector implements TraceCollector, TraceReporter {

    private InfluxDB influxDB;
    private InfluxConnection influxConnection;
    private OkHttpClient.Builder clientBuilder;
    private List<TagProvider> tagProviders;
    private List<FieldProvider> fieldProviders;

    private List<Trace> traces = Collections.synchronizedList(new ArrayList<>());

    public InfluxTraceCollector(InfluxConnection influxConnection, OkHttpClient.Builder clientBuilder, List<FieldProvider> fieldProviders, List<TagProvider> tagProviders) {
        this.tagProviders = tagProviders != null ? tagProviders : Collections.emptyList();
        this.fieldProviders = fieldProviders != null ? fieldProviders : Collections.emptyList();
        this.influxConnection = influxConnection;
        this.clientBuilder = clientBuilder;
        this.influxDB = connect();
    }

    @Override
    public void collect(Trace trace) {
        traces.add(trace);
    }

    @Override
    public void report() {
        List<Trace> collectedTraces;
        synchronized (traces) {
            collectedTraces = new ArrayList<>(traces);
            traces.clear();
        }

        if (!influxDB
                .describeDatabases()
                .contains(influxConnection.getDatabase())) {
            influxDB.createDatabase(influxConnection.getDatabase());
        }

        BatchPoints.Builder batchPoints = BatchPoints.database(influxConnection.getDatabase());

        collectedTraces
                .stream()
                .forEach((trace) -> {

                    final Point.Builder tracePoint = Point.measurement("trace");
                    handle(tracePoint, trace);
                    batchPoints.point(tracePoint.build());
                });

        influxDB.write(batchPoints.build());
    }

    private Point.Builder handle(Point.Builder tracePoint, Trace trace) {
        tracePoint.addField("traceId", trace.getTraceId());
        tracePoint.addField("host", trace.getHost());
        tracePoint.addField("hops", trace.getHops());
        tracePoint.addField("target", trace.getTarget() != null ? trace.getTarget() : "unknown");
        tracePoint.addField("source", trace.getSource() != null ? trace.getSource() : "unknown");
        tracePoint.addField("duration", trace.getDuration());

        tracePoint.tag("hostTag", trace.getHost());
        if (trace.getSource() != null && !"unknown".equals(trace.getSource())) {
            tracePoint.tag("sourceTag", trace.getSource());
        }
        if(trace.getTarget() != null && !"unknown".equals(trace.getTarget())) {
            tracePoint.tag("targetTag", trace.getTarget());
        }
        tracePoint.tag("environment", trace.getEnvironment());
        tracePoint.tag("reporter", trace.getReporter());
        tracePoint.tag("type", trace
                .getTraceType()
                .getType());
        tracePoint.tag("traceRequestType", trace
                .getTraceRequestType()
                .getTraceRequestType());

        tags(tracePoint, tagProviders);
        fields(tracePoint, fieldProviders);

        tracePoint.time(trace.getTimeStamp(), TimeUnit.MILLISECONDS);
        return tracePoint;
    }

    private void fields(Point.Builder tracePoint, List<FieldProvider> fieldProviders) {
        fieldProviders
                        .stream()
                        .map(FieldProvider::getFields)
                        .map(Map::entrySet)
                        .flatMap(Set::stream)
                        .filter(entry -> Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue()))
                        .forEach(entry -> tracePoint.addField(entry.getKey(), entry.getValue()));
    }

    private void tags(Point.Builder tracePoint, List<TagProvider> tagProviders) {
        tagProviders
                .stream()
                .map(TagProvider::getTags)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .filter(entry -> Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue()))
                .forEach(entry -> tracePoint.tag(entry.getKey(), entry.getValue()));
    }

    private InfluxDB connect() {
        return new InfluxDBImpl(influxConnection.getUrl(), influxConnection.getUser(), influxConnection.getPassword(), clientBuilder);
    }
}
