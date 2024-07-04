package codesquad;

import codesquad.http.handler.SocketHandler;
import codesquad.http.message.parser.*;
import codesquad.utils.SystemTimer;
import codesquad.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static HttpHeaderParser headerParser(){
        return new HttpHeaderParser();
    }
    private static HttpBodyParser bodyParser(){
        return new HttpBodyParser();
    }
    private static HttpQueryStringParser queryStringParser(){
        return new HttpQueryStringParser();
    }
    private static HttpRequestStartLineParser startLineParser(){
        return new HttpRequestStartLineParser();
    }
    private static HttpRequestParser requestParser(HttpRequestStartLineParser startLineParser,
                                                  HttpQueryStringParser queryStringParser,
                                                  HttpHeaderParser headerParser,
                                                  HttpBodyParser bodyParser){
        return new HttpRequestParser(startLineParser,headerParser,bodyParser,queryStringParser);
    }
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port); // 8080 포트에서 서버를 엽니다.
        logger.info("Listening for connection on port {}...",port);
        Timer timer = new SystemTimer();
        HttpRequestParser requestParser = requestParser(startLineParser(),queryStringParser(),headerParser(),bodyParser());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,100,10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            Socket clientSocket = null;
            try { // 클라이언트 연결을 수락합니다.
                System.out.println("waiting..");
                clientSocket = serverSocket.accept();
                System.out.println("accept!");
                SocketHandler handler = new SocketHandler(clientSocket,requestParser,timer);
                threadPoolExecutor.execute(handler);
            } catch(Exception e){
                logger.error(e.getMessage());
            } finally{
                if(clientSocket != null){
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        logger.error("Socket close exception",e.getMessage());
                    }
                }
            }
        }
    }
}
