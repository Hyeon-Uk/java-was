package codesquad.was.http.handler;

import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.Request;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.session.Session;

import java.net.URLDecoder;
import java.util.List;

public class XssRequestWrapper implements Request {
    private final Request request;
    public XssRequestWrapper(Request request) {
        this.request = request;
    }
    private String parseHtmlEscapeChar(String text){
        return text == null ? null : text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
    @Override
    public String getQueryString(String parameter) {
        return parseHtmlEscapeChar(request.getQueryString(parameter));
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public String getUri() {
        return parseHtmlEscapeChar(request.getUri());
    }

    @Override
    public String getHttpVersion() {
        return request.getHttpVersion();
    }

    @Override
    public List<String> getHeader(String key) {
        return request.getHeader(key);
    }

    @Override
    public byte[] getBody() {
        return request.getBody();
    }

    @Override
    public HttpFile getFile(String key) {
        return request.getFile(key);
    }

    @Override
    public List<Cookie> getCookies() {
        return request.getCookies();
    }

    @Override
    public Session getSession() {
        return request.getSession();
    }

    @Override
    public Session getSession(boolean create) {
        return request.getSession(create);
    }

    @Override
    public boolean isNewSession() {
        return request.isNewSession();
    }
}
