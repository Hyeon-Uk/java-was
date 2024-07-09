package codesquad.was.http.handler;

import codesquad.was.http.exception.HttpException;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.message.parser.HttpRequestParser;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocketHandler implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private final Socket socket;
    private final Timer timer;
    private final DateFormat dateFormatter;
    private final HttpRequestParser requestParser;
    private final RequestHandlerMapper requestHandlerMapper;

    public SocketHandler(Socket socket,
                         HttpRequestParser requestParser,
                         Timer timer,
                         DateFormat dateFormatter,
                         RequestHandlerMapper requestHandlerMapper) {
        this.socket = socket;
        this.requestParser = requestParser;
        this.timer = timer;
        this.dateFormatter = dateFormatter;
        this.requestHandlerMapper = requestHandlerMapper;
    }

    @Override
    public void run() {
        HttpResponse errorResponse = null;
        try (InputStream is = socket.getInputStream()) {
            try {
                String requestMessage = readRequestMessage(is);
                Map<String, List<String>> header = new HashMap<>();
                HttpRequest request = requestParser.parse(requestMessage);
                HttpResponse response = new HttpResponse(request.getHttpVersion(), header);

                RequestHandler handler = requestHandlerMapper.getRequestHandler(request.getUri());
                handler.handle(request, response);

                sendResponse(response,socket);
            }catch(HttpException httpException) {
                logger.error(httpException.getMessage());
                errorResponse = new HttpResponse(httpException);
                sendResponse(errorResponse,socket);
            }catch(Exception e){
                logger.error(e.getMessage());
                //internal server error response
                errorResponse = new HttpResponse(new HttpInternalServerErrorException("Internal Server Exception"));
                sendResponse(errorResponse,socket);
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

    private String readRequestMessage(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        do{
            length = is.read(buffer);
            sb.append(new String(buffer,0,length));
        }while(length == BUFFER_SIZE);
        return sb.toString();
    }

    private void sendResponse(HttpResponse response,Socket clientSocket) throws IOException {
        response.setHeader("Date",dateFormatter.format(timer.getCurrentTime()));

        clientSocket.getOutputStream().write(response.parse());
        clientSocket.getOutputStream().flush();
    }
}
