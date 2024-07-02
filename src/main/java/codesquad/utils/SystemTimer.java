package codesquad.utils;

import java.util.Date;

public class SystemTimer implements Timer {
    @Override
    public Date getCurrentTime() {
        return new Date();
    }
}
