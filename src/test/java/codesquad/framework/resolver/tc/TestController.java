package codesquad.framework.resolver.tc;

import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.annotation.RequestParam;
import codesquad.framework.resolver.annotation.SessionParam;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;

public class TestController {

    public void stringParamMethod(@RequestParam(name = "param") String param) {
    }

    public void intParamMethod(@RequestParam(name = "param") int param,@RequestParam(name="param2") Integer param2) {
    }

    public void longParamMethod(@RequestParam(name = "param") long param,@RequestParam(name="param2") Long param2) {
    }

    public void floatParamMethod(@RequestParam(name = "param") float param,@RequestParam(name="param2") Float param2) {
    }

    public void doubleParamMethod(@RequestParam(name = "param") double param,@RequestParam(name="param2") Double param2) {
    }

    public void objectParamMethod(@RequestParam(name = "param") TestObject param) {
    }

    public void sessionParamMethod(@SessionParam(create = true) Session session) {
    }

    public void sessionParamMethodWithFalse(@SessionParam(create = false) Session session) {
    }

    public void sessionParamMethodWithoutAnnotation(Session session) {
    }

    public void httpRequestMethod(HttpRequest request) {
    }

    public void httpResponseMethod(HttpResponse response) {
    }

    public void modelMethod(Model model){

    }

    public void testObjectMethod(@RequestParam TestObject testObject){

    }
}