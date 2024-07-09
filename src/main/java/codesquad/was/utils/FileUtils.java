package codesquad.was.utils;

import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.vo.MIME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private FileUtils(){}
    public static MIME getMIME(String path){
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        return MIME.fromExtension(extension);
    }
    public static byte[] readStaticFile(String path){
        try(InputStream is = FileUtils.class.getResourceAsStream("/static".concat(path))){
            return is.readAllBytes();
        }catch(IOException e){
            throw new HttpNotFoundException("could not find this static file : ".concat(path));
        }
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
