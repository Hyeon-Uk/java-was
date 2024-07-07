package codesquad.was.http.handler;

import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;


public interface RequestHandler {
    default void handle(HttpRequest req, HttpResponse res){
        switch(req.getMethod()){
            case GET->getHandle(req,res);
            case POST ->postHandle(req,res);
            case PUT ->putHandle(req,res);
            case PATCH -> patchHandle(req,res);
            case DELETE -> deleteHandle(req,res);
            case OPTIONS -> optionsHandle(req,res);
            case TRACE -> traceHandle(req,res);
        }
    }
    default void getHandle(HttpRequest req, HttpResponse res){}
    default void postHandle(HttpRequest req, HttpResponse res){}
    default void putHandle(HttpRequest req, HttpResponse res){}
    default void patchHandle(HttpRequest req, HttpResponse res){}
    default void deleteHandle(HttpRequest req, HttpResponse res){}
    default void optionsHandle(HttpRequest req, HttpResponse res){}
    default void traceHandle(HttpRequest req, HttpResponse res){}
}
