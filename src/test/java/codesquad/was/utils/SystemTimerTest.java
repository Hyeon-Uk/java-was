package codesquad.was.utils;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SystemTimerTest {
    Timer timer = new SystemTimer();

    //TODO : 시스템에 의존하는 구현 클래스를 정확히 테스트하는법은 뭘까요?
    @Test
    void getCurrentTime(){
        //given
        Date currentTime = timer.getCurrentTime();

        //when & then
        assertNotNull(currentTime);
    }
}