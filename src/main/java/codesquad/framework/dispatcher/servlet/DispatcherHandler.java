package codesquad.framework.dispatcher.servlet;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.exception.HttpException;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.request.Request;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Coffee(name="dispatcher")
public class DispatcherHandler implements RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(DispatcherHandler.class);
    private final RequestHandlerMapper requestHandlerMapper;
    private final HttpTemplateEngine engine;
    private final FileUtil fileUtil;

    public DispatcherHandler(RequestHandlerMapper requestHandlerMapper,
                             HttpTemplateEngine engine,
                             FileUtil fileUtil) {
        this.requestHandlerMapper = requestHandlerMapper;
        this.engine = engine;
        this.fileUtil = fileUtil;
    }

    @Override
    public void handle(Request req, HttpResponse res) {
        try{
            String path = req.getUri();
            HttpMethod method = req.getMethod();
            RequestHandler requestHandler = requestHandlerMapper.getRequestHandler(path, method);
            requestHandler.handle(req,res);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            Model model = new Model();
            model.addAttribute("errorMessage",e.getMessage());
            byte[] files = fileUtil.readFile("/templates/error.html");
            String template = new String(files);

            try {
                res.setBody(engine.render(template, model.asMap()));
                res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
