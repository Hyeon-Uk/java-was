package codesquad.was.http.message.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpFileTest {
    @Nested
    @DisplayName("isImageFile")
    class isImageFile {
        @ParameterizedTest
        @EnumSource(value = MIME.class, mode = EnumSource.Mode.INCLUDE, names = {"JPG", "JPEG", "PNG", "GIF"})
        void success(MIME mime) {
            //given
            String contentType = mime.getMimeType();
            String fileName = "fileName." + mime.getExtension();
            byte[] data = new byte[0];

            //when
            HttpFile file = new HttpFile(contentType, fileName, data);

            //then
            assertTrue(file.isImageFile());
        }

        @ParameterizedTest
        @EnumSource(value = MIME.class, mode = EnumSource.Mode.EXCLUDE, names = {"JPG", "JPEG", "PNG", "GIF"})
        void failure(MIME mime) {
            //given
            String contentType = mime.getMimeType();
            String fileName = "fileName." + mime.getExtension();
            byte[] data = new byte[0];

            //when
            HttpFile file = new HttpFile(contentType, fileName, data);

            //then
            assertFalse(file.isImageFile());
        }
    }

    @Nested
    @DisplayName("isExist")
    class IsExist {
        @ParameterizedTest
        @NullAndEmptySource
        void emptyAndNullContentType(String contentType) {
            //given
            HttpFile file = new HttpFile(contentType, "fileName.jpg", new byte[1]);

            //when & then
            assertFalse(file.isExist());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void emptyAndNullFileName(String fileName) {
            //given
            HttpFile file = new HttpFile(MIME.GIF.getMimeType(), fileName, new byte[1]);

            //when & then
            assertFalse(file.isExist());
        }

        @Test
        void emptyData() {
            //given
            HttpFile file = new HttpFile(MIME.GIF.getMimeType(), "text.gif", new byte[0]);

            //when & then
            assertFalse(file.isExist());
        }

        @Test
        void nullData() {
            //given
            HttpFile file = new HttpFile(MIME.GIF.getMimeType(), "text.gif", null);

            //when & then
            assertFalse(file.isExist());
        }
    }
}