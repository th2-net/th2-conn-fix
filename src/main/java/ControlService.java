import com.exactpro.th2.conn.grpc.ConnGrpc.ConnImplBase;
import com.exactpro.th2.conn.grpc.Response;
import com.exactpro.th2.conn.grpc.StartRequest;
import com.exactpro.th2.conn.grpc.StopRequest;
import io.grpc.stub.StreamObserver;
import quickfix.ConfigError;

import static com.exactpro.th2.conn.grpc.Response.Status.FAILURE;
import static com.exactpro.th2.conn.grpc.Response.Status.SUCCESS;

class ControlService extends ConnImplBase {
    private final ClientController controller;

    public ControlService(ClientController controller) {
        this.controller = controller;
    }

    @Override
    public void start(StartRequest request, StreamObserver<Response> observer) {
        synchronized (this) {
            try {
                if (controller.isRunning) {
                    observer.onNext(failure("Already running"));
                } else {
                    controller.start(request.getStopAfter());
                    if (request.getStopAfter() > 0)
                        observer.onNext(success("Started with scheduled stop after " + request.getStopAfter() + " seconds"));
                    else observer.onNext(success("Successfully started"));
                }
                observer.onCompleted();
            } catch (RuntimeException | ConfigError e) {
                observer.onError(e);
            }
        }
    }


    @Override
    public void stop(StopRequest request, StreamObserver<Response> observer) {
        synchronized (this) {
            try {
                if (!controller.isRunning) observer.onNext(failure("Already stopped"));
                else {
                    controller.stop();
                    observer.onNext(success("Successfully stopped"));
                }
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        }
    }


    private Response success(String message) {
        return Response.newBuilder().setStatus(SUCCESS).setMessage(message).build();
    }

    private Response failure(String message) {
        return Response.newBuilder().setStatus(FAILURE).setMessage(message).build();
    }
}