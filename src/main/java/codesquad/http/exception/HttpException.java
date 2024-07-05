package codesquad.http.exception;

import codesquad.http.message.response.HttpStatus;

public class HttpException extends RuntimeException {
    private final HttpStatus status;
    private final String errorMessage;
    public HttpException(HttpStatus status,String errorMessage){
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
