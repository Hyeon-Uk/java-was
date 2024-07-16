package codesquad;

import codesquad.framework.coffee.CoffeeShop;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.was.Server;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.handler.RequestHandlerMapper;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.session.Session;
import codesquad.was.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private Object[] resolveArguments(HttpRequest req, HttpResponse res,Model model,Method method){
        return Arrays.stream(method.getParameters())
                .map(param ->{
                    Class<?> type = param.getType();
                    if(HttpRequest.class.equals(type)) return req;
                    else if(HttpResponse.class.equals(type)) return res;
                    else if(Session.class.equals(type)) return req.getSession(false);
                    else if(Model.class.equals(type)) return model;
                    else return null;
                }).toArray();
    }


    private RequestHandler makeRequestHandler(Object bean, Method method, HttpStatus status, FileUtil fileUtil, HttpTemplateEngine engine) {
        return (req, res) -> {
            try {
                Model model = new Model();
                Object[] args = resolveArguments(req, res,model, method);
                Class<?> returnType = method.getReturnType();
                //view 처리
                if(returnType.equals(String.class)){
                    String viewPath = (String)method.invoke(bean, args);

                    if(viewPath.startsWith("redirect:")){
                        String redirectPath = viewPath.substring("redirect:".length());

                        res.sendRedirect(redirectPath);
                        return;
                    }

                    if(!viewPath.startsWith("/")){
                        viewPath = "/" + viewPath;
                    }
                    String fullViewPath = viewPath.concat(".html");
                    String html = new String(fileUtil.readStaticFile(fullViewPath));
                    String rendered = engine.render(html, model.asMap());

                    res.setStatus(status);
                    res.setBody(rendered.getBytes());
                }
                else{
                    method.invoke(bean,args);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to invoke method " + method + " on " + bean, e);
            }
        };
    }

    public Main(int port) throws Exception {
        CoffeeShop coffeeShop = new CoffeeShop();
        RequestHandlerMapper requestHandlerMapper = coffeeShop.getBean(RequestHandlerMapper.class);
        HttpTemplateEngine engine = coffeeShop.getBean(HttpTemplateEngine.class);
        FileUtil fileUtil = coffeeShop.getBean(FileUtil.class);
        List<?> controllerClasses = coffeeShop.getAllBeansOfAnnotation(Controller.class);
        for (Object controllerClass : controllerClasses) {
            Arrays.stream(controllerClass.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                    .forEach(method -> {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        requestHandlerMapper.setRequestHandler(annotation.path(), annotation.method(), makeRequestHandler(controllerClass, method,annotation.status(),fileUtil,engine));
                    });
        }

        Server server = coffeeShop.getBean(Server.class);

        server.start();
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new Main(port);
    }
}
