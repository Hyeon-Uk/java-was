package codesquad.framework.dispatcher.servlet;

import codesquad.framework.ObjectMapper;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.ArgumentResolver;
import codesquad.was.http.exception.HttpException;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.request.Request;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ResponseBodyHandler implements RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(ResponseBodyHandler.class);
    private final Object controller;
    private final Method method;
    private final HttpStatus status;
    private final ArgumentResolver argumentResolver;
    private final ObjectMapper objectMapper;

    public ResponseBodyHandler(Object controller,
                      Method method,
                      HttpStatus status,
                      ArgumentResolver argumentResolver,
                               ObjectMapper objectMapper) {
        this.controller = controller;
        this.method = method;
        this.status = status;
        this.argumentResolver = argumentResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(Request req, HttpResponse res) {
        try{
            Model model = new Model();
            Object[] arguments = argumentResolver.resolveArguments(method,req,res,model);
            Class<?> returnType = method.getReturnType();
            Object result = method.invoke(controller, arguments);
            if(Void.class.equals(returnType)){
                res.setStatus(HttpStatus.NO_CONTENT);
            }
            else {
                res.setStatus(status);
                res.setHeader("Content-Type","application/json;charset=UTF-8");
                res.setBody(objectMapper.toJson(result).getBytes());
            }
        } catch (InvocationTargetException e) {
            Throwable originalException = e.getTargetException();
            if(originalException instanceof HttpException){
                throw (HttpException) originalException;
            }
            else{
                throw new HttpInternalServerErrorException(originalException.getMessage());
            }
        } catch (Exception e) {
            throw new HttpInternalServerErrorException(e.getMessage());
        }
    }

}
