package codesquad.framework.dispatcher.servlet;

import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.ArgumentResolver;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.exception.HttpException;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MvcHandler implements RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(MvcHandler.class);
    private final Object controller;
    private final Method method;
    private final HttpStatus status;
    private final FileUtil fileUtil;
    private final HttpTemplateEngine templateEngine;
    private final ArgumentResolver argumentResolver;

    public MvcHandler(Object controller,
                      Method method,
                      HttpStatus status,
                      FileUtil fileUtil,
                      HttpTemplateEngine templateEngine,
                      ArgumentResolver argumentResolver) {
        this.controller = controller;
        this.method = method;
        this.status = status;
        this.fileUtil = fileUtil;
        this.templateEngine = templateEngine;
        this.argumentResolver = argumentResolver;
    }

    @Override
    public void handle(HttpRequest req, HttpResponse res) {
        try{
            Model model = new Model();
            Object[] arguments = argumentResolver.resolveArguments(method,req,res,model);

            String viewPath = (String)method.invoke(controller,arguments);

            if(viewPath!= null &&  viewPath.startsWith("redirect:")) {
                res.sendRedirect(extractRedirectUrl(viewPath));
                return;
            }

            String rendered = renderTemplate(viewPath,model.asMap());

            res.setStatus(status);
            res.setBody(rendered.getBytes());
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

    private String extractRedirectUrl(String viewPath) {
        return viewPath.substring("redirect:".length());
    }

    private String renderTemplate(String viewPath, Map<String,Object> attribute) throws IllegalAccessException {
        if(!viewPath.startsWith("/")) {
            viewPath = "/" + viewPath;
        }
        String viewFile = viewPath.concat(".html");
        String template = new String(fileUtil.readFile("/templates"+viewFile));
        return templateEngine.render(template,attribute);
    }
}
