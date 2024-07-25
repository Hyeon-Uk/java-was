package codesquad.framework.mock;

import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.vo.MIME;
import codesquad.was.utils.FileUtil;

public class MockFileUtil implements FileUtil {

    @Override
    public MIME getMIME(String filePath) {
        int index = filePath.lastIndexOf(".");
        if(index == -1) return null;
        String substring = filePath.substring(index + 1);

        return MIME.fromExtension(substring);
    }

    @Override
    public byte[] readFile(String path) {
        if (path.equals("/static/test.html")) {
            return "<html><body><h1>Test Content</h1></body></html>".getBytes();
        } else if (path.equals("/images/image.png")) {
            return new byte[]{0x39, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        } else {
            throw new HttpNotFoundException("not found");
        }
    }

    @Override
    public boolean isFilePath(String uri) {
        // 테스트에서는 간단히 구현
        return uri.startsWith("/static") || uri.startsWith("/images");
    }
}