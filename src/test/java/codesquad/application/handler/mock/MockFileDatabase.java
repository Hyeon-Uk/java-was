package codesquad.application.handler.mock;

import codesquad.middleware.FileDatabase;
import codesquad.was.http.message.vo.HttpFile;

import java.util.ArrayList;
import java.util.List;

public class MockFileDatabase implements FileDatabase {
    private List<HttpFile> files = new ArrayList<>();
    @Override
    public String save(HttpFile file) {
        files.add(file);
        return file.getFileName();
    }

    @Override
    public byte[] getFileData(String savedPath) {
        return new byte[0];
    }
}
