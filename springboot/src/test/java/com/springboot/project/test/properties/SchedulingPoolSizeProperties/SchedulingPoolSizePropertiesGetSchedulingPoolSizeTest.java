package com.springboot.project.test.properties.SchedulingPoolSizeProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SchedulingPoolSizePropertiesGetSchedulingPoolSizeTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(50,
                this.schedulingPoolSizeProperties.getSchedulingPoolSize());
    }

}
