package com.springboot.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilUTCStringTest extends BaseTest {

    @Test
    public void test() {
        var result = this.timeZoneUtil.UTCString();
        assertEquals("+00:00", result);
    }

}
