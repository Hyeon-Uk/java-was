package codesquad.message.mock;

import codesquad.utils.Timer;

import java.util.Date;

public class MockTimer implements Timer {
    private long mockTime;
    public MockTimer(long mockTime) {
        this.mockTime = mockTime;
    }
    @Override
    public Date getCurrentTime() {
        return new Date(this.mockTime);
    }
}
