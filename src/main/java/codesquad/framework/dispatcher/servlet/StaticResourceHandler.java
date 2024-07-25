package codesquad.framework.dispatcher.servlet;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.middleware.FileDatabase;
import codesquad.middleware.FileSystemDatabase;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.request.Request;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.utils.FileUtil;

@Coffee(name="staticHandler")
public class StaticResourceHandler implements RequestHandler {
    private final FileUtil fileUtil;
    private final FileDatabase fileDatabase;

    public StaticResourceHandler(FileUtil fileUtil, FileDatabase fileDatabase) {
        this.fileUtil = fileUtil;
        this.fileDatabase = fileDatabase;
    }

    @Override
    public void handle(Request req, HttpResponse res) {
        String uri = req.getUri();
        byte[] data = null;
        if(uri.contains("/images")){
            String name = uri.replace("/images/","");
            data = fileDatabase.getFileData(name);
        }
        else {
            data = fileUtil.readFile("/static" + req.getUri());
        }
        res.setHeader("Content-Type",fileUtil.getMIME(req.getUri()).getMimeType());
        res.setBody(data);
        res.setStatus(HttpStatus.OK);
    }
}
