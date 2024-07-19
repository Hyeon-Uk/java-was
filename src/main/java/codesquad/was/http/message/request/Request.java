package codesquad.was.http.message.request;


import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.session.Session;

import java.util.List;

public interface Request {
    String getQueryString(String parameter);

    public HttpMethod getMethod() ;

    public String getUri() ;

    public String getHttpVersion() ;

    List<String> getHeader(String key) ;

    byte[] getBody() ;

    HttpFile getFile(String key);

    List<Cookie> getCookies() ;

    Session getSession();
    Session getSession(boolean create);
    boolean isNewSession();
}

