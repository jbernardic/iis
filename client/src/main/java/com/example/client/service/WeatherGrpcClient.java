package com.example.client.service;

import com.example.client.config.ClientProperties;
import com.example.client.model.WeatherRow;
import com.example.iis.grpc.weather.CityWeather;
import com.example.iis.grpc.weather.TemperatureReply;
import com.example.iis.grpc.weather.TemperatureRequest;
import com.example.iis.grpc.weather.WeatherServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherGrpcClient {

    private final ManagedChannel channel;
    private final WeatherServiceGrpc.WeatherServiceBlockingStub stub;

    public WeatherGrpcClient(ClientProperties props) {
        this.channel = ManagedChannelBuilder
                .forAddress(props.getGrpc().getHost(), props.getGrpc().getPort())
                .usePlaintext()
                .build();
        this.stub = WeatherServiceGrpc.newBlockingStub(channel);
    }

    public Result getTemperature(String query) {
        TemperatureReply reply = stub.getTemperature(
                TemperatureRequest.newBuilder().setQuery(query == null ? "" : query).build());

        List<WeatherRow> rows = new ArrayList<>();
        for (CityWeather c : reply.getResultsList()) {
            rows.add(new WeatherRow(
                    c.getCity(),
                    c.getTemperature(),
                    c.getHumidity(),
                    c.getPressure(),
                    (c.getWindDirection() + " " + c.getWindSpeed()).trim(),
                    c.getWeather()));
        }
        return new Result(reply.getMeasuredAt(), rows);
    }

    public record Result(String measuredAt, List<WeatherRow> rows) {
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
