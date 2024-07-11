package codesquad.application.handler;

import codesquad.message.mock.MockTimer;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.session.SessionManager;
import codesquad.was.http.session.SessionStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockFactory {
    public static HttpRequest getHttpRequest(HttpMethod method,Map<String,String>queryString, Map<String, List<String>> header,String body){
        return new HttpRequest(new HttpRequestStartLine("HTTP/1.1","",method),
                queryString,
                new HttpHeader(header),
                new HttpBody(body),
                new SessionManager(new SessionStorage(),new MockTimer(10l)));
    }
    public static HttpRequest getHttpRequest(HttpMethod method){
        return getHttpRequest(method,new HashMap<>(),new HashMap<>(),"");
    }
    public static HttpResponse getHttpResponse(){
        return new HttpResponse("HTTP/1.1",new HashMap<>());
    }
}
