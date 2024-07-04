package codesquad.http.handler;

import codesquad.http.message.parser.HttpRequestParser;
import codesquad.http.message.request.HttpRequestMessage;
import codesquad.http.message.response.HttpResponseMessage;
import codesquad.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class SocketHandler implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private final Socket socket;
    private final Timer timer;
    private final HttpRequestParser requestParser;
    private final RequestHandlerMapper requestHandlerMapper;

    public SocketHandler(Socket socket,
                         HttpRequestParser requestParser,
                         Timer timer,
                         RequestHandlerMapper requestHandlerMapper) {
        this.socket = socket;
        this.requestParser = requestParser;
        this.timer = timer;
        this.requestHandlerMapper = requestHandlerMapper;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            String requestMessage = readRequestMessage(br);
            Map<String, List<String>> header = new HashMap<>();
            HttpRequestMessage request = requestParser.parse(requestMessage);
            HttpResponseMessage response = new HttpResponseMessage(request.getHttpVersion(), header);

            RequestHandler handler = requestHandlerMapper.getRequestHandler(request.getUri());
            handler.handle(request, response);

            response.setHeader("Date", getFormattedDate());
            response.setHeader("Content-Length", Integer.toString(response.getBody().length));
            byte[] parse = response.parse();
            socket.getOutputStream().write(parse);
            socket.getOutputStream().flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                logger.error("Socket Close Exception");
            }
        }
    }

    private String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat.format(timer.getCurrentTime());
    }

    private String readRequestMessage(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
