package codesquad.application.handler.mock;

import codesquad.application.utils.PasswordEncoder;

public class MockPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    @Override
    public boolean match(String str, String encoded) {
        return new StringBuilder(str).reverse().toString().equals(encoded);
    }
}
