package com.example.iis.grpc;

import com.example.iis.config.AppProperties;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Starts and stops the embedded gRPC server alongside the Spring context
 * (Part 4). Running the server manually (rather than via a starter) keeps it
 * independent of the Spring Boot version.
 */
@Component
public class GrpcServer implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    private final AppProperties props;
    private final WeatherServiceImpl weatherService;

    private Server server;
    private volatile boolean running;

    public GrpcServer(AppProperties props, WeatherServiceImpl weatherService) {
        this.props = props;
        this.weatherService = weatherService;
    }

    @Override
    public void start() {
        int port = props.getGrpc().getPort();
        try {
            server = ServerBuilder.forPort(port)
                    .addService(weatherService)
                    .build()
                    .start();
            running = true;
            log.info("gRPC weather server started on port {}", port);
        } catch (Exception e) {
            throw new IllegalStateException("Could not start gRPC server on port " + port, e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
            log.info("gRPC weather server stopped");
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
