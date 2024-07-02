package codesquad.http.message;

/***
 * 잘못된 형식의 request message는 서버를 죽일 수 있기 때문에, 서버가 죽지 않도록 예외처리를 강제해야한다고 생각함
 */
public class InvalidRequestFormatException extends RuntimeException{
    public InvalidRequestFormatException() {
        super("Invalid request format");
    }
}
