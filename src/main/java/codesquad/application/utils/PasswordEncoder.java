package codesquad.application.utils;

public interface PasswordEncoder {
    String encode(String str);
    boolean match(String str,String encoded);
}
