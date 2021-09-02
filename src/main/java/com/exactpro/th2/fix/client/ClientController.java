package com.exactpro.th2.fix.client;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

class ClientController implements AutoCloseable {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Future<?> stopFuture = CompletableFuture.completedFuture(null);
    private final FixClient client;

    ClientController(@NotNull FixClient client) {
        this.client = Objects.requireNonNull(client, "Fix Client must not be null");
    }

    public synchronized void start(int stopAfter) {
        if (!isRunning()) {
            client.start();
            if (stopAfter > 0) {
                stopFuture = executor.schedule(client::stop, stopAfter, SECONDS);
            }
        }
    }

    public synchronized void stop() {
        if (isRunning()) {
            stopFuture.cancel(true);
            client.stop();
        }
    }

    @Override
    public synchronized void close() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(5, SECONDS)) executor.shutdownNow();
    }

    public boolean isRunning() {
        return client.isRunning();
    }

}
