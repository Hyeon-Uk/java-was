package codesquad.http.handler;

import codesquad.http.message.request.HttpRequestMessage;
import codesquad.http.message.response.HttpResponseMessage;
import codesquad.http.message.response.HttpStatus;
import codesquad.utils.FileUtils;

public class StaticResourceHandler implements RequestHandler {
    private final static String resourceBasePath = System.getProperty("user.dir").concat("/src/main/resources/static/");

    @Override
    public void getHandle(HttpRequestMessage req, HttpResponseMessage res) {
        res.setHeader("Content-Type", FileUtils.getContentType(req.getUri()));
        String absPath = resourceBasePath.concat(req.getUri().substring(1));
        res.setBody(FileUtils.extractFileData(absPath));
        res.setStatus(HttpStatus.OK);
    }
}
