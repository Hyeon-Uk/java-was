package codesquad.was.http.exception;

import codesquad.was.http.message.response.HttpStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HttpExceptionTest {
    @ParameterizedTest
    @MethodSource("provideExceptionsWithStatus")
    void exceptionTest(HttpException exception, HttpStatus expectedStatus){
        assertEquals(expectedStatus, exception.getStatus());
    }

    private static Stream<Arguments> provideExceptionsWithStatus(){
        return Stream.of(
                Arguments.of(new HttpBadRequestException(""), HttpStatus.BAD_REQUEST),
                Arguments.of(new HttpInternalServerErrorException(""),HttpStatus.INTERNAL_SERVER_ERROR),
                Arguments.of(new HttpMethodNotAllowedException(""),HttpStatus.METHOD_NOT_ALLOWED),
                Arguments.of(new HttpNotFoundException(""),HttpStatus.NOT_FOUND)
        );
    }
}