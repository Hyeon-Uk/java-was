package codesquad.was.utils;

import codesquad.framework.coffee.annotation.Coffee;

import java.util.Date;

@Coffee
public class SystemTimer implements Timer {
    @Override
    public Date getCurrentTime() {
        return new Date();
    }
}
