package codesquad.http.handler;

import codesquad.http.exception.HttpException;
import codesquad.http.exception.HttpInternalServerErrorException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        HttpResponseMessage errorResponse = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            try {
                String requestMessage = readRequestMessage(br);
                Map<String, List<String>> header = new HashMap<>();
                HttpRequestMessage request = requestParser.parse(requestMessage);
                HttpResponseMessage response = new HttpResponseMessage(request.getHttpVersion(), header);

                RequestHandler handler = requestHandlerMapper.getRequestHandler(request.getUri());
                handler.handle(request, response);

                byte[] parse = response.parse(timer);
                socket.getOutputStream().write(parse);
                socket.getOutputStream().flush();
            }catch(HttpException httpException) {
                logger.error(httpException.getMessage());
                errorResponse = new HttpResponseMessage(httpException);
                socket.getOutputStream().write(errorResponse.parse(timer));
                socket.getOutputStream().flush();
            }catch(Exception e){
                logger.error(e.getMessage());
                //internal server error response
                errorResponse = new HttpResponseMessage(new HttpInternalServerErrorException("Internal Server Exception"));
                socket.getOutputStream().write(errorResponse.parse(timer));
                socket.getOutputStream().flush();
            }finally{
                socket.close();
            }
        } catch(Exception e){
            logger.error(e.getMessage());
        } finally{
            if(socket!=null&&!socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
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
