package codesquad.http.message.parser;

import codesquad.was.http.message.parser.HttpBodyParser;
import codesquad.was.http.message.vo.HttpBody;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpBodyParserTest {
    private final HttpBodyParser parser = new HttpBodyParser();
    @Test
    void bodyParsingWithStringTest(){
        //given
        String body = "hello world";

        //when
        HttpBody parse = parser.parse(body);

        //then
        assertEquals(new String(parse.getBody()),body);
    }

    @Test
    void bodyParsingWithByteArrayTest(){
        //given
        byte[] body = "hello world".getBytes();

        //when
        HttpBody parse = parser.parse(body);

        //then
        assertEquals(parse.getBody(),body);
    }
}