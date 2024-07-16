package codesquad.was.utils;

import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.vo.MIME;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {

    @Nested
    class GetMIMETests {
        @ParameterizedTest
        @EnumSource(MIME.class)
        public void testGetMIME_withValidExtensions(MIME mime) {
            // Given a file path with the extension corresponding to MIME
            String path = "/path/to/file." + mime.getExtension();

            // When calling getMIME method
            FileUtils fileUtils = new FileUtils();
            MIME result = fileUtils.getMIME(path);

            // Then assert that the MIME type is correct
            assertEquals(mime, result);
            assertEquals(mime.getMimeType(), result.getMimeType());
        }

        @ParameterizedTest
        @EnumSource(value = MIME.class, names = {"TXT", "JPEG", "PDF"})
        public void testGetMIME_withSpecificExtensions(MIME mime) {
            // Given a file path with the extension corresponding to MIME
            String path = "/path/to/file." + mime.getExtension();

            // When calling getMIME method
            FileUtils fileUtils = new FileUtils();
            MIME result = fileUtils.getMIME(path);

            // Then assert that the MIME type is correct
            assertEquals(mime, result);
            assertEquals(mime.getMimeType(), result.getMimeType());
        }

        @ParameterizedTest
        @EnumSource(value = MIME.class, mode = EnumSource.Mode.EXCLUDE, names = {"TXT", "JPEG", "PDF"})
        public void testGetMIME_withExcludedExtensions(MIME mime) {
            // Given a file path with the extension not corresponding to MIME
            String path = "/path/to/file." + mime.getExtension();

            // When calling getMIME method
            FileUtils fileUtils = new FileUtils();
            MIME result = fileUtils.getMIME(path);

            // Then assert that the MIME type is default or appropriate for files with no extension
            assertNotNull(result); // Adjust assertion as per your requirement
        }

        @Test
        void notFilePath(){
            //given
            String path = "/path/to/api/end/point";

            //when
            FileUtils fileUtils = new FileUtils();
            MIME mime = fileUtils.getMIME(path);

            //then
            assertNull(mime);
        }
    }

    @Nested
    class ReadStaticFileTests {

        @Test
        public void testReadStaticFile_withExistingFile() {
            // Given an existing file path
            String path = "/input.txt"; // Example path to an existing static file

            // When calling readStaticFile method
            FileUtils fileUtils = new FileUtils();
            byte[] fileData = fileUtils.readStaticFile(path);

            // Then assert that the returned byte array is not empty
            assertTrue(fileData.length > 0);
        }

        @Test
        public void testReadStaticFile_withNonExistingFile() {
            // Given a non-existing file path
            String path = "/nonexistentfile.txt"; // Example path to a non-existing static file

            // When calling readStaticFile method, expect HttpNotFoundException
            FileUtils fileUtils = new FileUtils();
            assertThrows(HttpNotFoundException.class, () -> fileUtils.readStaticFile(path));
        }
    }

    @Nested
    class IsFilePathTests {

        @Test
        public void testIsFilePath_withFilePath() {
            // Given a URI that represents a file path
            String uri = "/input.txt"; // Example file path

            // When calling isFilePath method
            FileUtils fileUtils = new FileUtils();
            boolean result = fileUtils.isFilePath(uri);

            // Then assert that the result is true
            assertTrue(result);
        }

        @Test
        public void testIsFilePath_withNoFilePath() {
            // Given a URI that does not represent a file path
            String uri = "/path/to/resource"; // Example URI without file path

            // When calling isFilePath method
            FileUtils fileUtils = new FileUtils();
            boolean result = fileUtils.isFilePath(uri);

            // Then assert that the result is false
            assertFalse(result);
        }
    }
}
