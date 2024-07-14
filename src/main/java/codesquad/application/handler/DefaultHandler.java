package codesquad.application.handler;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.was.http.exception.HttpMethodNotAllowedException;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.utils.FileUtil;
import codesquad.was.utils.FileUtils;

@Coffee(name = "default")
public class DefaultHandler implements RequestHandler {
    private final FileUtil fileUtil;

    public DefaultHandler(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public void getHandle(HttpRequest req, HttpResponse res){
        String uri = req.getUri();
        if(uri.lastIndexOf("/") == uri.length()-1){
            uri = uri.concat("index.html");
        }
        else{
            uri = uri.concat("/index.html");
        }
        try {
            byte[] body = fileUtil.readStaticFile(uri);
            res.setBody(body);
            res.setStatus(HttpStatus.OK);
        }catch(Exception e){
            throw new HttpNotFoundException(req.getUri().concat(" : request can not found"));
        }
    }
}
