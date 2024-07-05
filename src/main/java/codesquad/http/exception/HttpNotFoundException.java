package codesquad.http.exception;

import codesquad.http.message.response.HttpStatus;

public class HttpNotFoundException extends Http4XXException{
    public HttpNotFoundException(String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorMessage);
    }
}
