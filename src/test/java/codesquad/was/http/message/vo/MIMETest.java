package codesquad.was.http.message.vo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class MIMETest {
    @ParameterizedTest
    @CsvSource({
            "txt, text/plain",
            "html, text/html",
            "htm, text/html",
            "json, application/json",
            "xml, application/xml",
            "jpeg, image/jpeg",
            "jpg, image/jpeg",
            "png, image/png",
            "gif, image/gif",
            "pdf, application/pdf",
            "doc, application/msword",
            "docx, application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "xls, application/vnd.ms-excel",
            "xlsx, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "ppt, application/vnd.ms-powerpoint",
            "pptx, application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "zip, application/zip",
            "rar, application/x-rar-compressed"
    })
    void testGetMimeTypeByExtension(String extension, String expectedMimeType) {
        MIME mime = MIME.fromExtension(extension);
        assertNotNull(mime);
        assertEquals(expectedMimeType, mime.getMimeType());
    }

    @ParameterizedTest
    @CsvSource({
            "TXT, text/plain",
            "JpG, image/jpeg",
            "Pdf, application/pdf"
    })
    void testGetMimeTypeByExtensionCaseInsensitive(String extension, String expectedMimeType) {
        MIME mime = MIME.fromExtension(extension);
        assertNotNull(mime);
        assertEquals(expectedMimeType, mime.getMimeType());
    }

    @ParameterizedTest
    @CsvSource({
            "unknown,",
            ",",
    })
    void testGetMimeTypeByExtensionNotFound(String extension) {
        System.out.println(extension == null);
        assertNull(MIME.fromExtension(extension));
    }

    @ParameterizedTest
    @EnumSource(MIME.class)
    public void testGetExtension(MIME mimeType) {
        assertEquals(mimeType.getExtension(), MIME.valueOf(mimeType.name()).getExtension());
    }

    @ParameterizedTest
    @EnumSource(MIME.class)
    public void testGetMimeType(MIME mimeType) {
        assertEquals(mimeType.getMimeType(), MIME.valueOf(mimeType.name()).getMimeType());
    }
}