package codesquad.was.http.message;

public class InvalidResponseFormatException extends RuntimeException{
    public InvalidResponseFormatException() {
        super("Invalid resopnse format");
    }
}
