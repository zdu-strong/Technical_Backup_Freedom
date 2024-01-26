package com.springboot.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilGetTimeZoneTest extends BaseTest {

    @Test
    public void test() {
        var result = this.timeZoneUtil.getTimeZone("Asia/Shanghai");
        assertEquals("+08:00", this.timeZoneUtil.getTimeZoneString(result));
    }

}
