package codesquad;

import codesquad.framework.coffee.CoffeeShop;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.was.Server;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.handler.RequestHandlerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private RequestHandler makeRequestHandler(Object bean, Method method) {
        return (req, res) -> {
            try {
                method.invoke(bean, req, res);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to invoke method " + method + " on " + bean, e);
            }
        };
    }

    public Main(int port) throws Exception {
        CoffeeShop coffeeShop = new CoffeeShop();
        RequestHandlerMapper requestHandlerMapper = coffeeShop.getBean(RequestHandlerMapper.class);
        List<?> controllerClasses = coffeeShop.getAllBeansOfAnnotation(Controller.class);
        for (Object controllerClass : controllerClasses) {
            Arrays.stream(controllerClass.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                    .forEach(method -> {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        requestHandlerMapper.setRequestHandler(annotation.path(), annotation.method(), makeRequestHandler(controllerClass, method));
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
