package codesquad.middleware;

import codesquad.was.http.message.vo.HttpFile;

public interface FileDatabase {
    String save(HttpFile file);
    byte[] getFileData(String savedPath);
}
