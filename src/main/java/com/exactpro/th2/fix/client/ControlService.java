package com.exactpro.th2.fix.client;

import com.exactpro.th2.conn.grpc.ConnGrpc.ConnImplBase;
import com.exactpro.th2.conn.grpc.Response;
import com.exactpro.th2.conn.grpc.StartRequest;
import com.exactpro.th2.conn.grpc.StopRequest;
import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.exactpro.th2.conn.grpc.Response.Status.FAILURE;
import static com.exactpro.th2.conn.grpc.Response.Status.SUCCESS;
import static io.grpc.Status.INTERNAL;

class ControlService extends ConnImplBase {

    private final ClientController controller;

    public ControlService(@NotNull ClientController controller) {
        this.controller = Objects.requireNonNull(controller, "Client Controller must not be null!");
    }

    @Override
    public synchronized void start(StartRequest request, StreamObserver<Response> observer) {

        try {
            if (controller.isRunning()) {
                observer.onNext(failure("Already running"));
            } else {
                controller.start(request.getStopAfter());
                if (request.getStopAfter() > 0) {
                    observer.onNext(success("Started with scheduled stop after " + request.getStopAfter() + " seconds"));
                } else {
                    observer.onNext(success("Successfully started"));
                }
            }
            observer.onCompleted();
        } catch (RuntimeException e) {
            observer.onError(wrapException(e));
        }
    }


    @Override
    public synchronized void stop(StopRequest request, StreamObserver<Response> observer) {

        try {
            if (!controller.isRunning()) {
                observer.onNext(failure("Already stopped"));
            } else {
                controller.stop();
                observer.onNext(success("Successfully stopped"));
            }
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(wrapException(e));
        }
    }


    private Throwable wrapException(Throwable e) {
        return INTERNAL.withCause(e).withDescription(e.getMessage()).asRuntimeException();
    }

    private Response success(String message) {
        return Response.newBuilder().setStatus(SUCCESS).setMessage(message).build();
    }

    private Response failure(String message) {
        return Response.newBuilder().setStatus(FAILURE).setMessage(message).build();
    }
}