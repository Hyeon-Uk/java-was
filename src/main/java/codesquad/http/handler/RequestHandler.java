package codesquad.http.handler;

import codesquad.http.message.request.HttpRequestMessage;
import codesquad.http.message.response.HttpResponseMessage;


public interface RequestHandler {
    default void handle(HttpRequestMessage req, HttpResponseMessage res){
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
    default void getHandle(HttpRequestMessage req,HttpResponseMessage res){}
    default void postHandle(HttpRequestMessage req,HttpResponseMessage res){}
    default void putHandle(HttpRequestMessage req,HttpResponseMessage res){}
    default void patchHandle(HttpRequestMessage req,HttpResponseMessage res){}
    default void deleteHandle(HttpRequestMessage req,HttpResponseMessage res){}
    default void optionsHandle(HttpRequestMessage req,HttpResponseMessage res){}
    default void traceHandle(HttpRequestMessage req,HttpResponseMessage res){}
}
