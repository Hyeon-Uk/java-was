package codesquad.was.utils;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.vo.MIME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Coffee
public class FileUtils implements FileUtil {
    public MIME getMIME(String path){
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        return MIME.fromExtension(extension);
    }
    public byte[] readStaticFile(String path){
        try(InputStream is = FileUtils.class.getResourceAsStream("/static".concat(path))){
            return is.readAllBytes();
        }catch(Exception e){
            throw new HttpNotFoundException("could not find this static file : ".concat(path));
        }
    }
    public boolean isFilePath(String uri){
        return uri.lastIndexOf('.') != -1;
    }
}
