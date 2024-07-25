package codesquad.was.http.message.parser;

import codesquad.was.http.message.request.HttpRequest;

import java.io.InputStream;

public interface RequestParser {
    HttpRequest parse(String message);
    HttpRequest parse(InputStream is);
}
