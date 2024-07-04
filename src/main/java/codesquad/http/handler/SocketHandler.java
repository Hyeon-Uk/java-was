package codesquad.http.handler;

import codesquad.http.message.InvalidResponseFormatException;
import codesquad.http.message.request.HttpRequestMessage;
import codesquad.http.message.response.HttpResponseMessage;
import codesquad.http.message.response.HttpStatus;
import codesquad.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketHandler implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private final static String resourceBasePath = System.getProperty("user.dir").concat("/src/main/resources/static/");
    private final Socket socket;
    private final Timer timer;
    public SocketHandler(Socket socket,Timer timer) {
        this.socket = socket;
        this.timer = timer;
    }

    @Override
    public void run() {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            String requestMessage = readRequestMessage(br);
            HttpRequestMessage request = new HttpRequestMessage(requestMessage);

            HttpResponseMessage response = handle(request);

            socket.getOutputStream().write(response.parseMessage());
            socket.getOutputStream().flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        } finally{
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Socket close exception",e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private HttpResponseMessage handle(HttpRequestMessage request) throws Exception {
        return switch(request.getMethod()){
            case GET -> getSomething(request);
            case POST -> null;
            case PUT -> null;
            case DELETE -> null;
            case PATCH -> null;
            case HEAD -> null;
            case OPTIONS -> optionsSomething(request);
            case TRACE -> null;
            case CONNECT -> null;
        };
    }

    private HttpResponseMessage optionsSomething(HttpRequestMessage request) throws InvalidResponseFormatException {
        Map<String,String> header = new HashMap<>();
        header.put("Access-Control-Allow-Origin","*/*");
        return new HttpResponseMessage.Builder(HttpStatus.OK,header,timer)
                .build();
    }

    private boolean isFileRequest(String uri){
        return uri.lastIndexOf('.') != -1;
    }
    private byte[] extractFileData(String uri) throws Exception {
        String substring = uri.substring(1);
        File file = new File(resourceBasePath.concat(substring));

        FileInputStream fis =  new FileInputStream(file);

        return fis.readAllBytes();
    }

    private String getContentType(String path){
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }

        return switch (extension) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "svg" -> "image/svg+xml";
            case "ico" -> "image/x-icon";
            default -> "*/*";
        };
    }

    private HttpResponseMessage getSomething(HttpRequestMessage request) throws Exception {
        if(isFileRequest(request.getUri())){
            Map<String,String> header = new HashMap<>();
            header.put("Content-Type",getContentType(request.getUri()));
            return new HttpResponseMessage.Builder(HttpStatus.OK,header,timer)
                    .body(extractFileData(request.getUri()))
                    .build();
        }
        else{
            //일단은
            Map<String,String> header = new HashMap<>();
            header.put("Content-Type",getContentType(request.getUri()));
            return new HttpResponseMessage.Builder(HttpStatus.OK,header,timer)
                    .body("<h1>hello world!</h1>")
                    .build();
        }
    }

    private String readRequestMessage(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        while(true){
            String line = br.readLine();
            if(line == null || line.isEmpty()){
                break;
            }
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
