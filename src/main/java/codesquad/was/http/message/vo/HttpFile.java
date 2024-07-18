package codesquad.was.http.message.vo;

import java.util.List;

public class HttpFile {
    private String contentType;
    private String fileName;
    private byte[] data;

    public HttpFile(){}

    public HttpFile(String contentType, String fileName, byte[] data) {
        this();
        this.contentType = contentType;
        this.fileName = fileName;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isExist(){
        return data != null && data.length > 0 && fileName != null && !fileName.isEmpty()
                && contentType != null && !contentType.isEmpty();
    }

    public boolean isImageFile(){
        List<MIME> images = List.of(MIME.GIF,MIME.JPEG,MIME.PNG,MIME.JPG);

        return images.stream()
                .anyMatch(mime->mime.equals(MIME.fromMimeType(contentType)));
    }
}
