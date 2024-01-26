package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionFormatDateAsYearMonthDayHourMinuteTest extends BaseTest {
    private String timeZone;

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.formatDateAsYearMonthDayHourMinute(new Date(), this.timeZone);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.timeZone = this.timeZoneUtil.getTimeZoneString("Asia/Shanghai");
    }

}
