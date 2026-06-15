package com.example.iis.grpc.weather;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Weather lookup backed by DHMZ (https://vrijeme.hr/hrvatska_n.xml).
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class WeatherServiceGrpc {

  private WeatherServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "weather.WeatherService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.iis.grpc.weather.TemperatureRequest,
      com.example.iis.grpc.weather.TemperatureReply> getGetTemperatureMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTemperature",
      requestType = com.example.iis.grpc.weather.TemperatureRequest.class,
      responseType = com.example.iis.grpc.weather.TemperatureReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.iis.grpc.weather.TemperatureRequest,
      com.example.iis.grpc.weather.TemperatureReply> getGetTemperatureMethod() {
    io.grpc.MethodDescriptor<com.example.iis.grpc.weather.TemperatureRequest, com.example.iis.grpc.weather.TemperatureReply> getGetTemperatureMethod;
    if ((getGetTemperatureMethod = WeatherServiceGrpc.getGetTemperatureMethod) == null) {
      synchronized (WeatherServiceGrpc.class) {
        if ((getGetTemperatureMethod = WeatherServiceGrpc.getGetTemperatureMethod) == null) {
          WeatherServiceGrpc.getGetTemperatureMethod = getGetTemperatureMethod =
              io.grpc.MethodDescriptor.<com.example.iis.grpc.weather.TemperatureRequest, com.example.iis.grpc.weather.TemperatureReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetTemperature"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.iis.grpc.weather.TemperatureRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.iis.grpc.weather.TemperatureReply.getDefaultInstance()))
              .setSchemaDescriptor(new WeatherServiceMethodDescriptorSupplier("GetTemperature"))
              .build();
        }
      }
    }
    return getGetTemperatureMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WeatherServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherServiceStub>() {
        @java.lang.Override
        public WeatherServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherServiceStub(channel, callOptions);
        }
      };
    return WeatherServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static WeatherServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherServiceBlockingV2Stub>() {
        @java.lang.Override
        public WeatherServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return WeatherServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WeatherServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherServiceBlockingStub>() {
        @java.lang.Override
        public WeatherServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherServiceBlockingStub(channel, callOptions);
        }
      };
    return WeatherServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WeatherServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherServiceFutureStub>() {
        @java.lang.Override
        public WeatherServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherServiceFutureStub(channel, callOptions);
        }
      };
    return WeatherServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Weather lookup backed by DHMZ (https://vrijeme.hr/hrvatska_n.xml).
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Returns the current temperature for every city whose name contains the
     * given (case-insensitive) query string. If several cities match, all of
     * them are returned.
     * </pre>
     */
    default void getTemperature(com.example.iis.grpc.weather.TemperatureRequest request,
        io.grpc.stub.StreamObserver<com.example.iis.grpc.weather.TemperatureReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetTemperatureMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service WeatherService.
   * <pre>
   * Weather lookup backed by DHMZ (https://vrijeme.hr/hrvatska_n.xml).
   * </pre>
   */
  public static abstract class WeatherServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return WeatherServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service WeatherService.
   * <pre>
   * Weather lookup backed by DHMZ (https://vrijeme.hr/hrvatska_n.xml).
   * </pre>
   */
  public static final class WeatherServiceStub
      extends io.grpc.stub.AbstractAsyncStub<WeatherServiceStub> {
    private WeatherServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns the current temperature for every city whose name contains the
     * given (case-insensitive) query string. If several cities match, all of
     * them are returned.
     * </pre>
     */
    public void getTemperature(com.example.iis.grpc.weather.TemperatureRequest request,
        io.grpc.stub.StreamObserver<com.example.iis.grpc.weather.TemperatureReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTemperatureMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service WeatherService.
   * <pre>
   * Weather lookup backed by DHMZ (https://vrijeme.hr/hrvatska_n.xml).
   * </pre>
   */
  public static final class WeatherServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<WeatherServiceBlockingV2Stub> {
    private WeatherServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns the current temperature for every city whose name contains the
     * given (case-insensitive) query string. If several cities match, all of
     * them are returned.
     * </pre>
     */
    public com.example.iis.grpc.weather.TemperatureReply getTemperature(com.example.iis.grpc.weather.TemperatureRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetTemperatureMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service WeatherService.
   * <pre>
   * Weather lookup backed by DHMZ (https://vrijeme.hr/hrvatska_n.xml).
   * </pre>
   */
  public static final class WeatherServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<WeatherServiceBlockingStub> {
    private WeatherServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns the current temperature for every city whose name contains the
     * given (case-insensitive) query string. If several cities match, all of
     * them are returned.
     * </pre>
     */
    public com.example.iis.grpc.weather.TemperatureReply getTemperature(com.example.iis.grpc.weather.TemperatureRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetTemperatureMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service WeatherService.
   * <pre>
   * Weather lookup backed by DHMZ (https://vrijeme.hr/hrvatska_n.xml).
   * </pre>
   */
  public static final class WeatherServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<WeatherServiceFutureStub> {
    private WeatherServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns the current temperature for every city whose name contains the
     * given (case-insensitive) query string. If several cities match, all of
     * them are returned.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.iis.grpc.weather.TemperatureReply> getTemperature(
        com.example.iis.grpc.weather.TemperatureRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetTemperatureMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_TEMPERATURE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_TEMPERATURE:
          serviceImpl.getTemperature((com.example.iis.grpc.weather.TemperatureRequest) request,
              (io.grpc.stub.StreamObserver<com.example.iis.grpc.weather.TemperatureReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetTemperatureMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.iis.grpc.weather.TemperatureRequest,
              com.example.iis.grpc.weather.TemperatureReply>(
                service, METHODID_GET_TEMPERATURE)))
        .build();
  }

  private static abstract class WeatherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WeatherServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.iis.grpc.weather.WeatherProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WeatherService");
    }
  }

  private static final class WeatherServiceFileDescriptorSupplier
      extends WeatherServiceBaseDescriptorSupplier {
    WeatherServiceFileDescriptorSupplier() {}
  }

  private static final class WeatherServiceMethodDescriptorSupplier
      extends WeatherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    WeatherServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WeatherServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WeatherServiceFileDescriptorSupplier())
              .addMethod(getGetTemperatureMethod())
              .build();
        }
      }
    }
    return result;
  }
}
