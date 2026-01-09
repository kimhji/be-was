package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static ExecutorService executor;


    public static void main(String args[]) throws Exception {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        logger.info("Available processors: {}", cpuCount);

        executor = new ThreadPoolExecutor(
                cpuCount,
                cpuCount,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );

        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }
        RequestHandler.init();

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                Socket currentConnection = connection;
                executor.execute(() -> {
                    new RequestHandler(currentConnection).run();
                });
            }
        }
        finally {
            executor.shutdown();
        }
    }
}
