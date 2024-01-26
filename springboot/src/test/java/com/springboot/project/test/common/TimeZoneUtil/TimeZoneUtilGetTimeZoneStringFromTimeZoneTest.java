package com.springboot.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilGetTimeZoneStringFromTimeZoneTest extends BaseTest {

    private TimeZone timeZone;

    @Test
    public void test() {
        var result = this.timeZoneUtil.getTimeZoneString(this.timeZone);
        assertEquals("+08:00", result);
    }

    @BeforeEach
    public void beforeEach() {
        this.timeZone = this.timeZoneUtil.getTimeZone("Asia/Shanghai");
    }

}
