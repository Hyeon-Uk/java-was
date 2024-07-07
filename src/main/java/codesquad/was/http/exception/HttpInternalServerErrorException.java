package codesquad.was.http.exception;

import codesquad.was.http.message.response.HttpStatus;

public class HttpInternalServerErrorException extends Http5XXException{
    public HttpInternalServerErrorException(String errorMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}
