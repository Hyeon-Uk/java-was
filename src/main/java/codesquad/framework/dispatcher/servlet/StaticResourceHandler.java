package codesquad.framework.dispatcher.servlet;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.utils.FileUtil;

@Coffee(name="staticHandler")
public class StaticResourceHandler implements RequestHandler {
    private final FileUtil fileUtil;

    public StaticResourceHandler(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Override
    public void handle(HttpRequest req, HttpResponse res) {
        byte[] data = fileUtil.readFile("/static"+req.getUri());
        res.setHeader("Content-Type",fileUtil.getMIME(req.getUri()).getMimeType());
        res.setBody(data);
        res.setStatus(HttpStatus.OK);
    }
}
