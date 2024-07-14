package codesquad.was.http.handler;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.utils.FileUtil;
import codesquad.was.utils.FileUtils;

import java.io.*;

@Coffee
public class StaticResourceHandler implements RequestHandler {
    private final FileUtil fileUtil;

    public StaticResourceHandler(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        byte[] data = fileUtil.readStaticFile(req.getUri());
        res.setHeader("Content-Type",fileUtil.getMIME(req.getUri()).getMimeType());
        res.setBody(data);
        res.setStatus(HttpStatus.OK);
    }
}
