package com.springboot.project.test.common.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class TimeZoneUtilsGetTimeZoneFromUTCTest extends BaseTest {

    @Test
    public void test() {
        var result = this.timeZoneUtil.getTimeZoneFromUTC();
        assertEquals("+00:00", result);
    }

}
