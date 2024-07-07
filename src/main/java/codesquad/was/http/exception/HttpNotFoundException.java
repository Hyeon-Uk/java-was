package codesquad.was.http.exception;

import codesquad.was.http.message.response.HttpStatus;

public class HttpNotFoundException extends Http4XXException{
    public HttpNotFoundException(String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorMessage);
    }
}
