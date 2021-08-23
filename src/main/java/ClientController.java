import quickfix.ConfigError;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

class ClientController implements AutoCloseable {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Future<?> stopFuture = CompletableFuture.completedFuture(null);
    private final FixClient client;
    boolean isRunning;

    ClientController(FixClient client) {
        this.client = client;
        isRunning = client.isRunning();
    }

    public void start(int stopAfter) throws ConfigError {
        synchronized (this) {
            if (!isRunning) {
                client.start();
                if (stopAfter > 0) {
                    stopFuture = executor.schedule(client::stop, stopAfter, SECONDS); //cast to long?
                }
            }
        }
    }

    public void stop() {
        synchronized (this) {
            if (isRunning) {
                stopFuture.cancel(true);
                client.stop();
            }
        }
    }

    @Override
    public void close() throws InterruptedException {
        synchronized (this) {
            executor.shutdown();
            if (!executor.awaitTermination(5, SECONDS)) executor.shutdownNow();
        }
    }
}
