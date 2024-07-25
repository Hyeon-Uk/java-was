package codesquad.framework;

import codesquad.framework.coffee.CoffeeShop;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.dispatcher.servlet.MvcHandler;
import codesquad.framework.dispatcher.servlet.RequestHandlerMapper;
import codesquad.framework.dispatcher.servlet.ResponseBodyHandler;
import codesquad.framework.resolver.ArgumentResolver;
import codesquad.framework.resolver.annotation.ResponseBody;
import codesquad.was.Server;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.utils.FileUtil;

import java.util.Arrays;
import java.util.List;

public class Cafe {
    private final CoffeeShop coffeeShop;

    public Cafe() throws Exception {
        this("");
    }
    public Cafe(String basePackage) throws Exception {
        coffeeShop = new CoffeeShop(basePackage);// DI Container 생성 및 초기화
        registRequestHandler();
        startServer();
    }

    private void registRequestHandler() {
        RequestHandlerMapper requestHandlerMapper = coffeeShop.getBean(RequestHandlerMapper.class);
        HttpTemplateEngine engine = coffeeShop.getBean(HttpTemplateEngine.class);
        FileUtil fileUtil = coffeeShop.getBean(FileUtil.class);
        List<?> controllerClasses = coffeeShop.getAllBeansOfAnnotation(Controller.class);
        ArgumentResolver argResolver = coffeeShop.getBean(ArgumentResolver.class);
        ObjectMapper objectMapper = coffeeShop.getBean(ObjectMapper.class);
        for (Object controllerClass : controllerClasses) {
            Arrays.stream(controllerClass.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                    .forEach(method -> {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        if(method.isAnnotationPresent(ResponseBody.class)){
                            requestHandlerMapper.setRequestHandler(
                                    annotation.path(),
                                    annotation.method(),
                                    new ResponseBodyHandler(controllerClass,
                                            method,
                                            annotation.status(),
                                            argResolver,
                                            objectMapper)
                            );
                        }
                        else {
                            requestHandlerMapper.setRequestHandler(
                                    annotation.path(),
                                    annotation.method(),
                                    new MvcHandler(controllerClass,
                                            method,
                                            annotation.status(),
                                            fileUtil,
                                            engine,
                                            argResolver)
                            );
                        }
                    });
        }
    }

    private void startServer() throws Exception {
        Server server = coffeeShop.getBean(Server.class);
        server.start();
    }
}
