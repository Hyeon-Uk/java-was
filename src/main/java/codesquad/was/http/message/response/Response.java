package codesquad.was.http.message.response;

import codesquad.was.http.cookie.Cookie;

import java.util.List;

public interface Response {
    void sendRedirect(String redirectPath);
    void addCookie(Cookie cookie);
    void setStatus(HttpStatus status);
    void setHeader(String key,String value);
    void setBody(byte[] body);
    void setBody(String body);
    byte[] getBody();
    List<String> getHeaders(String key);
    HttpStatus getStatus();
    byte[] parse();
}
