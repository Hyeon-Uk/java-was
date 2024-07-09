package codesquad;

import codesquad.was.Server;
import codesquad.was.http.handler.RequestHandlerMapper;
import codesquad.was.http.message.parser.*;
import codesquad.was.utils.SystemTimer;
import codesquad.was.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private HttpHeaderParser headerParser() {
        return new HttpHeaderParser();
    }

    private HttpBodyParser bodyParser() {
        return new HttpBodyParser();
    }

    private HttpQueryStringParser queryStringParser() {
        return new HttpQueryStringParser();
    }

    private HttpRequestStartLineParser startLineParser() {
        return new HttpRequestStartLineParser();
    }

    private HttpRequestParser requestParser(HttpRequestStartLineParser startLineParser,
                                            HttpQueryStringParser queryStringParser,
                                            HttpHeaderParser headerParser,
                                            HttpBodyParser bodyParser) {
        return new HttpRequestParser(startLineParser, headerParser, bodyParser, queryStringParser);
    }

    private Timer timer() {
        return new SystemTimer();
    }

    private ExecutorService executorService() {
        return new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    private RequestHandlerMapper requestHandlerMapper() {
        return new RequestHandlerMapper();
    }

    private DateFormat dateFormat(){
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format;
    }

    private Server server(int port) throws IOException {
        return new Server(port,
                timer(),
                executorService(),
                requestParser(startLineParser(), queryStringParser(), headerParser(), bodyParser()),
                requestHandlerMapper(),
                dateFormat());
    }

    public Main(int port) throws IOException {
        Server server = server(port);

        server.start();
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        new Main(port);
    }
}
