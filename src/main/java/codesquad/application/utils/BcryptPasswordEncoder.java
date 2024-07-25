package codesquad.application.utils;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.UUID;

@Coffee
public class BcryptPasswordEncoder implements PasswordEncoder{
    private final Logger logger = LoggerFactory.getLogger(BcryptPasswordEncoder.class);

    @Override
    public String encode(String str) {
        String salt = generateSalt();
        return "$"+salt+"$"+encodeInner(str,salt);
    }

    private String encodeInner(String str,String salt){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            md.update(salt.getBytes());

            byte[] digest = md.digest();
            return byteArrayToHex(digest);
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new HttpInternalServerErrorException("some internal error");
        }
    }

    @Override
    public boolean match(String str, String encoded) {
        int start = encoded.indexOf("$");
        int end = encoded.lastIndexOf("$");
        if(start == -1 || end == -1 || start == end) return false;

        String salt = encoded.substring(start+1,end);
        String encodedHash = encoded.substring(end+1);
        String compare = encodeInner(str,salt);
        return compare.equals(encodedHash);
    }

    private String byteArrayToHex(byte[] bytes){
        if(bytes == null || bytes.length == 0) return null;

        StringBuilder sb = new StringBuilder(bytes.length*2);
        String hexNumber;
        for(int x =0;x<bytes.length;x++){
            hexNumber = "0"+Integer.toHexString(bytes[x]&0xff);
            sb.append(hexNumber.substring(hexNumber.length()-2));
        }
        return sb.toString();
    }

    private String generateSalt(){
        return UUID.randomUUID().toString();
    }
}
