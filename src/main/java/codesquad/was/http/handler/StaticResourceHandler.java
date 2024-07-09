package codesquad.was.http.handler;

import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.utils.FileUtils;

import java.io.*;

public class StaticResourceHandler implements RequestHandler {
    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        byte[] data = FileUtils.readStaticFile(req.getUri());
        res.setHeader("Content-Type",FileUtils.getMIME(req.getUri()).getMimeType());
        res.setBody(data);
        res.setStatus(HttpStatus.OK);
    }
}
