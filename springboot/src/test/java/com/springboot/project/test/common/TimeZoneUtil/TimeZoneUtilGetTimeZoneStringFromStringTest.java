package com.springboot.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilGetTimeZoneStringFromStringTest extends BaseTest {

    private String timeZone = "Asia/Shanghai";

    @Test
    public void test() {
        var result = this.timeZoneUtil.getTimeZoneString(timeZone);
        assertEquals("+08:00", result);
    }

}
