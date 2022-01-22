package com.exactpro.th2.fix.client;

import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GrpcServer {

        private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);
        private final Server server;

        public GrpcServer(Server server) throws IOException {
            this.server = server;
            this.server.start();
            LOGGER.info("'{}' started", GrpcServer.class.getSimpleName());
        }

        public void stop() throws InterruptedException {
            if (server.shutdown().awaitTermination(1, TimeUnit.SECONDS)) {
                LOGGER.warn("Server isn't stopped gracefully");
                server.shutdownNow();
            }
        }

        /**
         * Await termination on the main thread since the grpc library uses daemon threads.
         */
        public void blockUntilShutdown() throws InterruptedException {
            if (server != null) {
                server.awaitTermination();
            }
        }

}
