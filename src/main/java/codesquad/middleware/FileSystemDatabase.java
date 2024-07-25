package codesquad.middleware;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.message.vo.HttpFile;

import java.io.*;
import java.util.UUID;

@Coffee
public class FileSystemDatabase implements FileDatabase {
    private final String PATH = "images";
    public FileSystemDatabase(){}
    public String save(HttpFile file){
        File dir = new File(PATH);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                throw new HttpInternalServerErrorException("internal server error");
            }
        }
        String fileName = file.getFileName();
        int index = fileName.lastIndexOf(".");
        String extension = index == -1 ? null : fileName.substring(fileName.lastIndexOf("."));
        String storeName = UUID.randomUUID() + (extension != null ? extension :"");
        File saveFile = new File(dir, storeName);
        try(
            FileOutputStream fos = new FileOutputStream(saveFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
        ){
            bos.write(file.getData());
            return saveFile.getPath();
        }catch(Exception e){
            throw new HttpInternalServerErrorException("internal server error");
        }
    }

    public byte[] getFileData(String saveFileName){
        File file = new File(PATH+"/"+saveFileName);
        try{
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            return bis.readAllBytes();
        }catch(Exception e){
            throw new HttpInternalServerErrorException("internal server error");
        }
    }
}
