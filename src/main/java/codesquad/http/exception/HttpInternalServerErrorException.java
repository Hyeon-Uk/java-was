package codesquad.http.exception;

import codesquad.http.message.response.HttpStatus;

public class HttpInternalServerErrorException extends Http5XXException{
    public HttpInternalServerErrorException(String errorMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}
