package codesquad.http.exception;

import codesquad.http.message.response.HttpStatus;

public class HttpBadRequestException extends Http4XXException{
    public HttpBadRequestException( String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage);
    }
}
