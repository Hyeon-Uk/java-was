package codesquad.http.exception;

import codesquad.http.message.response.HttpStatus;

public class Http5XXException extends HttpException {
    public Http5XXException(HttpStatus status,String errorMessage){
        super(status, errorMessage);
    }
}
