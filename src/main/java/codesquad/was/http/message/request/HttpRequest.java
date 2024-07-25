package codesquad.was.http.message.request;

import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.session.Session;
import codesquad.was.http.session.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest implements Request {
    private final SessionManager sessionManager;
    private final HttpRequestStartLine startLine;
    private final HttpHeader header;
    private final HttpBody body;
    private final Map<String, String> queryString;

    public HttpRequest(HttpRequestStartLine startLine, Map<String, String> queryString, HttpHeader header, HttpBody body,SessionManager sessionManager) {
        this.startLine = startLine;
        this.queryString = queryString;
        this.header = header;
        this.body = body;
        this.sessionManager = sessionManager;
    }

    public String getQueryString(String parameter) {
        return queryString.get(parameter);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public String getHttpVersion() {
        return startLine.getHttpVersion();
    }

    public List<String> getHeader(String key) {
        return header.getHeaders(key);
    }

    public byte[] getBody() {
        return body.getBody();
    }

    public HttpFile getFile(String key){
        return body.getFile().get(key);
    }

    public List<Cookie> getCookies() {
        return parseCookieHeader(header.getHeaders("Cookie").isEmpty() ? "" : header.getHeaders("Cookie").get(0));
    }

    private List<Cookie> parseCookieHeader(String cookieHeader) {
        List<Cookie> cookies = new ArrayList<Cookie>();

        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            String[] cookieArray = cookieHeader.split(";");

            for (String cookieStr : cookieArray) {
                String[] cookieParts = cookieStr.split("=");

                String name = cookieParts[0].trim();
                String value = (cookieParts.length > 1) ? cookieParts[1].trim() : "";

                Cookie cookie = new Cookie(name, value);
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    public Session getSession(){
        return getSession(true);
    }
    public Session getSession(boolean create){
        return getCookies().stream()
                .filter(cookie -> "SID".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .flatMap(sessionManager::getSession)
                .orElseGet(()->create?sessionManager.createSession():null);
    }
    public boolean isNewSession(){
        return getCookies().stream()
                .noneMatch(cookie -> "SID".equals(cookie.getName()));
    }
}
