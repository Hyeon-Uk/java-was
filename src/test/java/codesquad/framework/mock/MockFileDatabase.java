package codesquad.framework.mock;

import codesquad.middleware.FileDatabase;
import codesquad.was.http.message.vo.HttpFile;

import java.util.HashMap;
import java.util.Map;

public class MockFileDatabase implements FileDatabase {
    @Override
    public String save(HttpFile file) {
        // 실제 파일을 저장하지 않고 임의의 저장 경로를 반환
        return "saved-path/" + file.getFileName();
    }

    @Override
    public byte[] getFileData(String savedPath) {
        if (savedPath.equals("image.png")) {
            return new byte[]{0x39, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        } else {
            return null; // Assume file not found
        }
    }
}
