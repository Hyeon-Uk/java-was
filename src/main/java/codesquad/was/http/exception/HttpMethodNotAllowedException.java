package codesquad.was.http.exception;

import codesquad.was.http.message.response.HttpStatus;

public class HttpMethodNotAllowedException extends Http4XXException{
    public HttpMethodNotAllowedException(String errorMessage) {
        super(HttpStatus.METHOD_NOT_ALLOWED, errorMessage);
    }
}
