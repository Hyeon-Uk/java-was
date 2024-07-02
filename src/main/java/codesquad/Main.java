package codesquad;

import codesquad.http.handler.SocketHandler;
import codesquad.utils.SystemTimer;
import codesquad.utils.Timer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class Main {
    private final static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080); // 8080 포트에서 서버를 엽니다.
        logger.info("Listening for connection on port 8080...");
        Timer timer = new SystemTimer();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,100,10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            try { // 클라이언트 연결을 수락합니다.
                Socket clientSocket = serverSocket.accept();
                SocketHandler handler = new SocketHandler(clientSocket,timer);
                threadPoolExecutor.execute(handler);
            } catch(IOException ioException){
                ioException.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
