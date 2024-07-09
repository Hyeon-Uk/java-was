package codesquad.message.response;

import codesquad.was.http.message.InvalidResponseFormatException;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.message.mock.MockTimer;
import codesquad.was.utils.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponseTest {
    private final String NEW_LINE = "\r\n";
    private String mockDateString = "Sat, 02 Jul 2024 13:15:30 GMT";
    private Timer mockTimer;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    @BeforeEach
    public void timerSetting() throws ParseException {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        mockTimer = new MockTimer(dateFormat.parse(mockDateString).getTime());
    }

    @Test
    public void verifyResponseMessage() throws InvalidResponseFormatException {
        //given
        HttpStatus status = HttpStatus.OK;
        String body = "<h1>helloworld</h1>";
        Map<String, List<String>> header = new HashMap<>();
        String contentType = "Content-Type";
        String contentTypeValue = "application/json; charset=utf-8";
        header.put(contentType,List.of(contentTypeValue));
        header.put("Date",List.of(dateFormat.format(mockTimer.getCurrentTime())));

        String expectedMessage =
                "HTTP/1.1 200 OK"+NEW_LINE
                +"Content-Length: 19"+NEW_LINE
                +"Date: Tue, 02 Jul 2024 13:15:30 GMT"+NEW_LINE
                +"Content-Type: application/json; charset=utf-8"+NEW_LINE
                +NEW_LINE
                +"<h1>helloworld</h1>";

        //when
        HttpResponse response = new HttpResponse("HTTP/1.1",header);
        response.setHeader("Date",dateFormat.format(mockTimer.getCurrentTime()));
        response.setStatus(status);
        response.setBody(body.getBytes());

        //then
        assertAll("response message validation",
                ()->assertTrue(response.getHeaders(contentType).contains("application/json; charset=utf-8")),
                ()->assertEquals(status,response.getStatus()),
                ()->assertEquals(body,new String(response.getBody())),
                ()->assertEquals(Integer.toString(body.length()),response.getHeaders("Content-Length").get(0)),
                ()->assertEquals(expectedMessage,new String(response.parse()))
                );
    }
}
