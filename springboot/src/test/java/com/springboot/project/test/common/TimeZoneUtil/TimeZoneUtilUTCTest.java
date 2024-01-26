package com.springboot.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilUTCTest extends BaseTest {

    @Test
    public void test() {
        var result = this.timeZoneUtil.UTC();
        assertNotNull(result);
        assertEquals("+00:00", this.timeZoneUtil.getTimeZoneString(result));
    }

}
