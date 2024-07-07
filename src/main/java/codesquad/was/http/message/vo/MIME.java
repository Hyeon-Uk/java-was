package codesquad.was.http.message.vo;

public enum MIME {
    TXT("txt", "text/plain"),
    HTML("html", "text/html"),
    HTM("htm", "text/html"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    JPEG("jpeg", "image/jpeg"),
    JPG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    GIF("gif", "image/gif"),
    PDF("pdf", "application/pdf"),
    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS("xls", "application/vnd.ms-excel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPT("ppt", "application/vnd.ms-powerpoint"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    ZIP("zip", "application/zip"),
    RAR("rar", "application/x-rar-compressed");

    private final String extension;
    private final String mimeType;

    MIME(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static MIME fromExtension(String ext) {
        for (MIME mime : values()) {
            if (mime.getExtension().equalsIgnoreCase(ext)) {
                return mime;
            }
        }
        return null;
    }
}
