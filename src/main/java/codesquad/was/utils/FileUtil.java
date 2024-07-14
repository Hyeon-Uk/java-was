package codesquad.was.utils;

import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.vo.MIME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface FileUtil {
    MIME getMIME(String path);
    byte[] readStaticFile(String path);
    boolean isFilePath(String uri);
}
