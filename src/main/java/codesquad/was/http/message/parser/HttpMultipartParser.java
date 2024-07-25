package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.message.vo.HttpHeader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Coffee
public class HttpMultipartParser {
    private final byte[] NEW_LINE = "\r\n".getBytes();

    public HttpBody parse(HttpHeader header, byte[] bodyBytes) {
        Map<String, String> params = new HashMap<>();
        Map<String, HttpFile> files = new HashMap<>();

        String contentType = header.getHeaders("Content-Type").get(0);
        String boundary = contentType.split("boundary=", 2)[1].trim();

        byte[] delimiter = ("--" + boundary).getBytes();
        byte[] endDelimiter = ("--" + boundary + "--").getBytes();

        int start = 0;
        int delimiterPos = indexOf(bodyBytes, delimiter, start);
        int endDelimiterPos = indexOf(bodyBytes, endDelimiter, start);
        while (delimiterPos != endDelimiterPos) {
            start = delimiterPos + delimiter.length + NEW_LINE.length; // part의 시작점
            delimiterPos = indexOf(bodyBytes, delimiter, start); // part의 경계

            int partHeaderEnd = indexOf(bodyBytes, NEW_LINE, start); // part header의 CRLF 위치
            if (partHeaderEnd != -1) {
                String contentDisposition = new String(Arrays.copyOfRange(bodyBytes, start, partHeaderEnd));

                if (contentDisposition.contains("filename=")) {
                    processFilePart(contentDisposition, bodyBytes, partHeaderEnd, delimiterPos, files);
                } else {
                    processParameterPart(contentDisposition, bodyBytes, partHeaderEnd, delimiterPos, params);
                }
            }
        }

        return new HttpBody(bodyBytes, files, params);
    }


    private int indexOf(byte[] body, byte[] target, int start) {
        for (int i = start; i < body.length - target.length; i++) {
            boolean found = true;
            for (int j = 0; j < target.length; j++) {
                if (body[i + j] != target[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

    private void processParameterPart(String contentDisposition, byte[] body, int partHeaderEnd,
                                      int delimiterPos, Map<String, String> params) {
        String name = getFieldName(contentDisposition.split(";")[1]);
        String data = new String(
                Arrays.copyOfRange(body, partHeaderEnd + NEW_LINE.length * 2, delimiterPos - NEW_LINE.length));
        params.put(name, data);
    }

    private void processFilePart(String contentDisposition, byte[] body, int partHeaderEnd,
                                 int delimiterPos, Map<String, HttpFile> files) {
        String name = getFieldName(contentDisposition.split(";")[1]);
        String filename = getFileName(contentDisposition.split(";")[2]);
        int contentTypeNewLinePos = indexOf(body, NEW_LINE,
                partHeaderEnd + NEW_LINE.length); // content-type \r\n 첫 위치
        String partContentType = new String(
                Arrays.copyOfRange(body, partHeaderEnd + NEW_LINE.length, contentTypeNewLinePos));
        partContentType = partContentType.split(":")[1].trim();
        byte[] fileBytes = Arrays.copyOfRange(
                body, contentTypeNewLinePos + NEW_LINE.length * 2, delimiterPos - NEW_LINE.length);
        files.put(name, new HttpFile(partContentType, filename, fileBytes));
    }

    private String getFieldName(String contentDisposition) {
        return contentDisposition.substring(contentDisposition.indexOf("name=") + "name=".length())
                .replaceAll("\"", "")
                .trim();
    }

    private String getFileName(String contentDisposition) {
        return contentDisposition.substring(contentDisposition.indexOf("filename=") + "filename=".length())
                .replaceAll("\"", "")
                .trim();
    }
}
