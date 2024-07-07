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
        try(InputStream resourceBasePath = StaticResourceHandler.class.getResourceAsStream("/static".concat(req.getUri()))) {
            res.setHeader("Content-Type", FileUtils.getContentType(req.getUri()));
            res.setBody(resourceBasePath.readAllBytes());
            res.setStatus(HttpStatus.OK);
        } catch (IOException e) {
            throw new HttpNotFoundException("could't find this static file");
        }
    }
}
