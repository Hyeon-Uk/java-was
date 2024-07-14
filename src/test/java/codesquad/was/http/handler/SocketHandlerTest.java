package codesquad.was.http.handler;

import codesquad.message.mock.MockTimer;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.exception.HttpMethodNotAllowedException;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.parser.RequestParser;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.session.SessionManager;
import codesquad.was.http.session.SessionStorage;
import codesquad.was.utils.CustomDateFormatter;
import codesquad.was.utils.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SocketHandlerTest {
    private Timer mockTimer;
    private ExecutorService mockExecutorService;
    private MockRequestParser mockRequestParser;
    private MockRequestHandlerMapper mockRequestHandlerMapper;
    private CustomDateFormatter mockDateFormat;
    private MockSocket mockSocket;
    private SocketHandler socketHandler;
    private final String NEW_LINE = "\r\n";

    private HttpRequest httpRequest =
            new HttpRequest(new HttpRequestStartLine("HTTP/1.1", "/", HttpMethod.GET),
                    new HashMap<>(),
                    new HttpHeader(new HashMap<>()),
                    new HttpBody(),
                    new SessionManager(new SessionStorage(), new MockTimer(10l)));

    private class MockRequestParser implements RequestParser {
        private boolean throwFlag;

        public void setThrowFlag(boolean throwFlag) {
            this.throwFlag = throwFlag;
        }

        @Override
        public HttpRequest parse(String message) {
            if (throwFlag) throw new InvalidRequestFormatException();
            return httpRequest;
        }
    }

    private class MockRequestHandlerMapper implements RequestHandlerMapper {
        private boolean throwMapperFlag;
        private boolean throwHandlerFlag;

        public void setThrowMapperFlag(boolean throwFlag) {
            this.throwMapperFlag = throwFlag;
        }

        public void setThrowHandlerFlag(boolean throwHandlerFlag) {
            this.throwHandlerFlag = throwHandlerFlag;
        }

        @Override
        public RequestHandler getRequestHandler(String path) {
            if (throwMapperFlag) throw new HttpNotFoundException("NotFound");
            return new RequestHandler() {
                @Override
                public void handle(HttpRequest req, HttpResponse res) {
                    if (throwHandlerFlag) throw new HttpMethodNotAllowedException("MethodNotAllowed");
                }
            };
        }
    }

    private class MockSocket extends Socket {
        private boolean inputStreamException;
        private boolean outputStreamException;
        private ByteArrayOutputStream os = new ByteArrayOutputStream(1024 * 10);

        public void setInputStreamException(boolean inputStreamException) {
            this.inputStreamException = inputStreamException;
        }

        public void setOutputStreamException(boolean outputStreamException) {
            this.outputStreamException = outputStreamException;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (inputStreamException) {
                throw new IOException("IOException");
            } else {
                return new ByteArrayInputStream("GET / HTTP/1.1\r\nDate: hello\r\n\r\n".getBytes());
            }
        }

        public int getOutputStreamSize() {
            return os.size();
        }

        public String getOutputData() {
            return new String(os.toByteArray());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            if (outputStreamException) {
                throw new IOException("IOException");
            } else {
                return os;
            }
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        mockTimer = new MockTimer(new Date().getTime());
        mockExecutorService = Executors.newFixedThreadPool(10);
        mockRequestParser = new MockRequestParser();
        mockRequestHandlerMapper = new MockRequestHandlerMapper();
        mockDateFormat = new CustomDateFormatter();
        mockSocket = new MockSocket();
        socketHandler = new SocketHandler(mockSocket,
                mockRequestParser,
                mockTimer,
                mockDateFormat,
                mockRequestHandlerMapper);
    }

    @AfterEach
    void tearDown() {
        mockExecutorService.shutdownNow();
    }

    /***
     * 테스트 해야할 목록
     *
     * 1. socket의 getInputStream() 메서드에서 ioexception이 터지는 경우
     * 2. request message의 format이 잘못된경우
     * 3. handler에서 exception을 throw 받은경우
     * 4. send 도중 ioexception이 발생한경우
     *
     */
    @Test
    void socketExceptionWithReadInputStream() throws IOException {
        //given
        mockSocket.setInputStreamException(true);

        //when
        socketHandler.run();

        //then
        assertTrue(mockSocket.isClosed());
        assertEquals(0, mockSocket.getOutputStreamSize());
    }

    @Test
    void unSatisfiedRequestMessageFormat() throws IOException {
        //given
        mockRequestParser.setThrowFlag(true);
        HttpResponse response = new HttpResponse(new HttpInternalServerErrorException("Internal Server Exception"));
        response.setHeader("Date", mockDateFormat.format(mockTimer.getCurrentTime()));
        String expectedResponseMessage = new String(response.parse());

        //when
        socketHandler.run();

        //then
        assertTrue(mockSocket.isClosed());
        assertEquals(expectedResponseMessage, mockSocket.getOutputData());
    }

//    @Test
//    void whenHandlerMapperThrowsException(){
//        //given
//        mockRequestHandlerMapper.setThrowMapperFlag(true);
//        HttpNotFoundException httpException = new HttpNotFoundException("NotFound");
//        HttpResponse response = new HttpResponse(httpException);
//        response.setHeader("Date", mockDateFormat.format(mockTimer.getCurrentTime()));
//        String expectedResponseMessage = new String(response.parse());
//
//        //when
//        socketHandler.run();
//
//        //then
//        assertTrue(mockSocket.isClosed());
//        assertEquals(expectedResponseMessage,mockSocket.getOutputData());
//    }

    @Test
    void socketExceptionWithWriteOutputStream() {
        //given
        mockSocket.setOutputStreamException(true);

        //when
        socketHandler.run();

        //then
        assertEquals(0, mockSocket.getOutputStreamSize());
    }
}