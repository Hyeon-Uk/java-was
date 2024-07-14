package codesquad.was.http.handler;

import codesquad.was.http.exception.HttpMethodNotAllowedException;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.utils.FileUtils;


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
            case HEAD -> headHandle(req,res);
            case CONNECT -> connectHandle(req,res);
        }
    }

    default void getHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void postHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void putHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void patchHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void deleteHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void optionsHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void traceHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void headHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
    default void connectHandle(HttpRequest req, HttpResponse res){
        throw new HttpMethodNotAllowedException("This method is not allowed");
    }
}
