package com.example.iis.grpc;

import com.example.iis.grpc.weather.CityWeather;
import com.example.iis.grpc.weather.TemperatureReply;
import com.example.iis.grpc.weather.TemperatureRequest;
import com.example.iis.grpc.weather.WeatherServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * gRPC implementation of the weather service (Part 4). Returns the current
 * temperature for every DHMZ city whose name contains the requested query.
 */
@Component
public class WeatherServiceImpl extends WeatherServiceGrpc.WeatherServiceImplBase {

    private final DhmzWeatherService dhmz;

    public WeatherServiceImpl(DhmzWeatherService dhmz) {
        this.dhmz = dhmz;
    }

    @Override
    public void getTemperature(TemperatureRequest request, StreamObserver<TemperatureReply> responseObserver) {
        try {
            String query = request.getQuery() == null ? "" : request.getQuery().trim().toLowerCase(Locale.ROOT);
            DhmzWeatherService.Snapshot snapshot = dhmz.getSnapshot();

            TemperatureReply.Builder reply = TemperatureReply.newBuilder()
                    .setMeasuredAt(snapshot.measuredAt());

            for (DhmzWeatherService.City city : snapshot.cities()) {
                if (query.isEmpty() || city.name().toLowerCase(Locale.ROOT).contains(query)) {
                    reply.addResults(CityWeather.newBuilder()
                            .setCity(city.name())
                            .setTemperature(city.temperature())
                            .setHumidity(city.humidity())
                            .setPressure(city.pressure())
                            .setWindDirection(city.windDirection())
                            .setWindSpeed(city.windSpeed())
                            .setWeather(city.weather())
                            .build());
                }
            }

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Weather lookup failed: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
