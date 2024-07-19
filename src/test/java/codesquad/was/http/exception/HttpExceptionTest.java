package codesquad.was.http.exception;

import codesquad.was.http.message.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("HttpException 클래스")
class HttpExceptionTest {

    @Nested
    @DisplayName("생성된 예외는")
    class CreatedExceptions {

        @ParameterizedTest(name = "{0}은 {1} 상태를 가진다")
        @MethodSource("provideHttpExceptionsWithStatus")
        @DisplayName("적절한 HTTP 상태를 가진다")
        void hasCorrectHttpStatus(Class<? extends HttpException> exceptionClass, HttpStatus expectedStatus) throws Exception {
            HttpException exception = exceptionClass.getDeclaredConstructor(String.class).newInstance("");
            assertEquals(expectedStatus, exception.getStatus());
        }

        private static Stream<Arguments> provideHttpExceptionsWithStatus() {
            return Stream.of(
                    Arguments.of(HttpBadRequestException.class, HttpStatus.BAD_REQUEST),
                    Arguments.of(HttpInternalServerErrorException.class, HttpStatus.INTERNAL_SERVER_ERROR),
                    Arguments.of(HttpMethodNotAllowedException.class, HttpStatus.METHOD_NOT_ALLOWED),
                    Arguments.of(HttpNotFoundException.class, HttpStatus.NOT_FOUND)
                    // 여기에 새로운 HttpException 클래스를 쉽게 추가할 수 있습니다.
            );
        }
    }
}