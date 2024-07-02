package codesquad.message.response;

import codesquad.http.message.InvalidResponseFormatException;
import codesquad.http.message.response.HttpResponseMessage;
import codesquad.http.message.response.HttpStatus;
import codesquad.message.mock.MockTimer;
import codesquad.utils.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponseMessageTest {
    private String mockDateString = "Sat, 02 Jul 2024 13:15:30 GMT";
    private Timer mockTimer;
    @BeforeEach
    public void timerSetting() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        mockTimer = new MockTimer(dateFormat.parse(mockDateString).getTime());
    }

    @Test
    public void verifyResponseMessage() throws InvalidResponseFormatException {
        //given
        HttpStatus status = HttpStatus.OK;
        String body = "<h1>helloworld</h1>";
        Map<String,String> header = new HashMap<>();
        String contentType = "Content-Type";
        String contentTypeValue = "application/json; charset=utf-8";
        header.put(contentType,contentTypeValue);

        String expectedMessage = """
                HTTP/1.1 200 OK
                Content-Length: 19
                Content-Type: application/json; charset=utf-8
                Date: Tue, 02 Jul 2024 13:15:30 GMT
                
                <h1>helloworld</h1>
                """;

        //when
        HttpResponseMessage response = new HttpResponseMessage.Builder(mockTimer)
                .status(status)
                .headers(header)
                .body(body)
                .build();

        //then
        assertAll("response message validation",
                ()->assertEquals(contentTypeValue,response.getHeader(contentType)),
                ()->assertEquals(status,response.getStatus()),
                ()->assertEquals(body,response.getBody()),
                ()->assertEquals(Integer.toString(body.length()),response.getHeader("Content-Length")),
                ()->assertEquals(expectedMessage,response.toString())
                );
    }

    @Test
    public void wrongResponseMessageWithoutStatusCode(){
        assertThrows(InvalidResponseFormatException.class,()->{
           new HttpResponseMessage.Builder(mockTimer)
                   .status(null)
                   .build();
        });
    }

    @Test
    public void wrongResponseMessageWithoutTimer(){
        assertThrows(InvalidResponseFormatException.class,()->{
            new HttpResponseMessage.Builder(null)
                    .status(HttpStatus.OK)
                    .build();
        });
    }
}
