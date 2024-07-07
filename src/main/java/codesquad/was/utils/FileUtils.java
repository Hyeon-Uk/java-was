package codesquad.was.utils;

import java.io.File;
import java.io.FileInputStream;

public class FileUtils {
    private FileUtils(){}
    public static String getContentType(String path){
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }

        return switch (extension) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "svg" -> "image/svg+xml";
            case "ico" -> "image/x-icon";
            default -> "*/*";
        };
    }

    public static boolean isFilePath(String uri){
        return uri.lastIndexOf('.') != -1;
    }

    public static byte[] extractFileData(String path){
        try {
            File file = new File(path);

            FileInputStream fis = new FileInputStream(file);

            return fis.readAllBytes();
        }catch(Exception e){
            throw new IllegalArgumentException("can not found ".concat(path));
        }
    }
}
