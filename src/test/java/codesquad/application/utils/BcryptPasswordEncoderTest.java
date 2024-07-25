package codesquad.application.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BcryptPasswordEncoderTest {
    private final PasswordEncoder passwordEncoder = new BcryptPasswordEncoder();

    @Test
    void encodeTest(){
        //given
        String password = "password";

        //when
        String encode = passwordEncoder.encode(password);

        //then
        assertNotEquals(password,encode);
        assertTrue(encode.indexOf("$") != encode.lastIndexOf("$"));
    }

    @Test
    void matchTest(){
        //given
        String password = "password";
        String encoded = passwordEncoder.encode(password);
        //when
        boolean match = passwordEncoder.match(password, encoded);

        //then
        assertTrue(match);
    }

    @Test
    void matchOtherTest(){
        //given
        String password = "password";
        String encoded = passwordEncoder.encode(password);

        //when
        boolean match = passwordEncoder.match("otherPassword", encoded);

        //then
        assertFalse(match);
    }
}