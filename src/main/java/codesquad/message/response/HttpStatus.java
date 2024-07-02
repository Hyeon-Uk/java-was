package codesquad.message.response;

public enum HttpStatus {
    OK(200,"OK"),CREATE(201,"Created"),ACCEPTED(202,"Accepted"),NO_CONTENT(204,"No Content"),
    MOVED_PERMANENTLY(301,"Moved Permanently"),NOT_MODIFIED(304,"Not Modified"),
    BAD_REQUEST(400,"Bad Request"),UNAUTHORIZED(401,"Unauthorized"),FORBIDDEN(403,"Forbidden"),NOT_FOUND(404,"Not Found"),
    METHOD_NOT_ALLOWED(405,"Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500,"Internal Server Error"),BAD_GATEWAY(502,"Bad Gateway"),GATEWAY_TIMEOUT(504,"Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505,"HTTP Version Not Supported");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
