package com.springboot.project.test.common.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class TimeZoneUtilsGetTimeZoneTest extends BaseTest {

    private String timeZone = "Asia/Shanghai";

    @Test
    public void test() {
        var result = this.timeZoneUtil.getTimeZone(this.timeZone);
        assertEquals("+08:00", result);
    }

}
