package codesquad.http.exception;

import codesquad.http.message.response.HttpStatus;

public class Http4XXException extends HttpException{
    public Http4XXException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
