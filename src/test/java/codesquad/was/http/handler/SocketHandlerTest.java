package codesquad.was.http.handler;

import codesquad.message.mock.MockTimer;
import codesquad.was.http.exception.HttpException;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.exception.HttpMethodNotAllowedException;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.parser.RequestParser;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.session.SessionManager;
import codesquad.was.http.session.SessionStorage;
import codesquad.was.utils.CustomDateFormatter;
import codesquad.was.utils.Timer;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SocketHandler 클래스")
class SocketHandlerTest {

    // Data
    private MockTimer mockTimer;
    private MockRequestParser mockRequestParser;
    private CustomDateFormatter mockDateFormat;
    private MockSocket mockSocket;
    private SocketHandler socketHandler;
    private MockRequestHandler mockRequestHandler;

    private HttpRequest httpRequest;

    // Context setup
    @BeforeEach
    void setUp() {
        mockTimer = new MockTimer(new Date().getTime());
        mockRequestParser = new MockRequestParser();
        mockDateFormat = new CustomDateFormatter();
        mockSocket = new MockSocket();
        mockRequestHandler = new MockRequestHandler();
        socketHandler = new SocketHandler(mockSocket, mockRequestParser, mockTimer, mockDateFormat, mockRequestHandler);

        httpRequest = new HttpRequest(
                new HttpRequestStartLine("HTTP/1.1", "/", HttpMethod.GET),
                new HashMap<>(),
                new HttpHeader(new HashMap<>()),
                new HttpBody(),
                new SessionManager(new SessionStorage(), mockTimer)
        );
    }

    @Nested
    @DisplayName("run 메소드는")
    class RunMethod {

        @Test
        @DisplayName("소켓의 InputStream에서 IOException이 발생하면 소켓을 닫는다")
        void socketExceptionWithReadInputStream() {
            mockSocket.setInputStreamException(true);

            socketHandler.run();

            assertTrue(mockSocket.isClosed());
            assertEquals(0, mockSocket.getOutputStreamSize());
        }

        @Test
        @DisplayName("잘못된 요청 형식을 받으면 500 응답을 반환한다")
        void unSatisfiedRequestMessageFormat() {
            mockRequestParser.setThrowFlag(true);
            HttpResponse expectedResponse = new HttpResponse(new HttpInternalServerErrorException("Internal Server Exception"));
            expectedResponse.setHeader("Date", mockDateFormat.format(mockTimer.getCurrentTime()));

            socketHandler.run();

            assertTrue(mockSocket.isClosed());
            assertEquals(new String(expectedResponse.parse()), mockSocket.getOutputData());
        }

        @Test
        @DisplayName("핸들러에서 예외가 발생하면 적절한 오류 응답을 반환한다")
        void whenHandlerThrowsException() {
            mockRequestHandler.setThrowFlag(true);
            HttpResponse expectedResponse = new HttpResponse(new HttpInternalServerErrorException("Internal Server Exception"));
            expectedResponse.setHeader("Date", mockDateFormat.format(mockTimer.getCurrentTime()));

            socketHandler.run();

            assertTrue(mockSocket.isClosed());
            assertEquals(new String(expectedResponse.parse()), mockSocket.getOutputData());
        }

        @Test
        @DisplayName("소켓의 OutputStream에서 IOException이 발생하면 응답을 보내지 않는다")
        void socketExceptionWithWriteOutputStream() {
            mockSocket.setOutputStreamException(true);

            socketHandler.run();

            assertEquals(0, mockSocket.getOutputStreamSize());
        }
    }

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

        @Override
        public HttpRequest parse(InputStream is) {
            if (throwFlag) throw new InvalidRequestFormatException();
            return httpRequest;
        }
    }

    private class MockRequestHandler implements RequestHandler {
        private boolean throwFlag;

        public void setThrowFlag(boolean throwFlag) {
            this.throwFlag = throwFlag;
        }

        @Override
        public void handle(HttpRequest req, HttpResponse res) {
            if(throwFlag) throw new HttpNotFoundException("NotFound");
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
}
