package de.effectivetrainings.observed.influx;

import de.effectivetrainings.observed.FieldProvider;
import de.effectivetrainings.observed.TagProvider;
import de.effectivetrainings.observed.TraceCollector;
import de.effectivetrainings.observed.TraceReporter;
import de.effectivetrainings.observed.model.RequestTrace;
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

    private List<RequestTrace> requestTraces = Collections.synchronizedList(new ArrayList<>());

    public InfluxTraceCollector(InfluxConnection influxConnection, OkHttpClient.Builder clientBuilder, List<FieldProvider> fieldProviders, List<TagProvider> tagProviders) {
        this.tagProviders = tagProviders != null ? tagProviders : Collections.emptyList();
        this.fieldProviders = fieldProviders != null ? fieldProviders : Collections.emptyList();
        this.influxConnection = influxConnection;
        this.clientBuilder = clientBuilder;
        this.influxDB = connect();
    }

    @Override
    public void collect(RequestTrace requestTrace) {
        requestTraces.add(requestTrace);
    }

    @Override
    public void report() {
        List<RequestTrace> collectedRequestTraces;
        synchronized (requestTraces) {
            collectedRequestTraces = new ArrayList<>(requestTraces);
            requestTraces.clear();
        }

        if (!influxDB
                .describeDatabases()
                .contains(influxConnection.getDatabase())) {
            influxDB.createDatabase(influxConnection.getDatabase());
        }

        BatchPoints.Builder batchPoints = BatchPoints.database(influxConnection.getDatabase());

        collectedRequestTraces
                .stream()
                .forEach((trace) -> {

                    final Point.Builder tracePoint = Point.measurement("trace");
                    handle(tracePoint, trace);
                    batchPoints.point(tracePoint.build());
                });

        influxDB.write(batchPoints.build());
    }

    private Point.Builder handle(Point.Builder tracePoint, RequestTrace requestTrace) {
        tracePoint.addField("traceId", requestTrace.getTraceId());
        tracePoint.addField("host", requestTrace.getHost());
        tracePoint.addField("hops", requestTrace.getHops());
        tracePoint.addField("target", requestTrace.getTarget() != null ? requestTrace.getTarget() : "unknown");
        tracePoint.addField("source", requestTrace.getSource() != null ? requestTrace.getSource() : "unknown");
        tracePoint.addField("duration", requestTrace.getDuration());

        tracePoint.tag("hostTag", requestTrace.getHost());
        if (requestTrace.getSource() != null && !"unknown".equals(requestTrace.getSource())) {
            tracePoint.tag("sourceTag", requestTrace.getSource());
        }
        if(requestTrace.getTarget() != null && !"unknown".equals(requestTrace.getTarget())) {
            tracePoint.tag("targetTag", requestTrace.getTarget());
        }
        tracePoint.tag("environment", requestTrace.getEnvironment());
        tracePoint.tag("reporter", requestTrace.getReporter());
        tracePoint.tag("type", requestTrace
                .getTraceType()
                .getType());
        tracePoint.tag("traceRequestType", requestTrace
                .getTraceRequestType()
                .getTraceRequestType());
        tracePoint.tag("traceIdTag", requestTrace.getTraceId());

        tags(tracePoint, tagProviders);
        fields(tracePoint, fieldProviders);

        tracePoint.time(requestTrace.getTimeStamp(), TimeUnit.MILLISECONDS);
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
