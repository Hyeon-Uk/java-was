package codesquad.was;

import codesquad.was.http.handler.RequestHandlerMapper;
import codesquad.was.http.handler.SocketHandler;
import codesquad.was.http.message.parser.HttpRequestParser;
import codesquad.was.http.message.parser.RequestParser;
import codesquad.was.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.concurrent.ExecutorService;

public class Server {
    private final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final Timer timer;
    private final ExecutorService threadPool;
    private final RequestParser httpRequestParser;
    private final RequestHandlerMapper requestHandlerMapper;
    private ServerSocket serverSocket;
    private DateFormat dateFormatter;
    public Server(int port,
                  Timer timer,
                  ExecutorService threadPool,
                  RequestParser httpRequestParser,
                  RequestHandlerMapper requestHandlerMapper,
                  DateFormat dateFormatter) throws IOException {
        this.port = port;
        this.timer = timer;
        this.threadPool = threadPool;
        this.serverSocket = new ServerSocket(port);
        logger.info("Server Socket binds on port: {}", port);
        this.httpRequestParser = httpRequestParser;
        this.requestHandlerMapper = requestHandlerMapper;
        this.dateFormatter = dateFormatter;
    }

    public void start() {
        logger.info("Listening for connection on port {}...", port);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                SocketHandler handler = new SocketHandler(clientSocket,
                        httpRequestParser,
                        timer,
                        dateFormatter,
                        requestHandlerMapper);

                threadPool.execute(handler);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

}
