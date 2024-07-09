package codesquad.was.http.message.parser;

import codesquad.was.http.message.request.HttpRequest;

public interface RequestParser {
    HttpRequest parse(String message);
}
