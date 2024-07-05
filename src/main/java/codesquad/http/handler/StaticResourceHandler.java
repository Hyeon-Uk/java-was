package codesquad.http.handler;

import codesquad.http.message.request.HttpRequestMessage;
import codesquad.http.message.response.HttpResponseMessage;
import codesquad.http.message.response.HttpStatus;
import codesquad.utils.FileUtils;

import java.io.*;

public class StaticResourceHandler implements RequestHandler {
    @Override
    public void getHandle(HttpRequestMessage req, HttpResponseMessage res) {
        try(InputStream resourceBasePath = StaticResourceHandler.class.getResourceAsStream("/static".concat(req.getUri()))) {
            res.setHeader("Content-Type", FileUtils.getContentType(req.getUri()));
            res.setBody(resourceBasePath.readAllBytes());
            res.setStatus(HttpStatus.OK);
        } catch (IOException e) {
            throw new IllegalArgumentException("could't find this static file");
        }
    }
}
