package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class SchedulingPoolSizeProperties {

    @Value("${spring.task.scheduling.pool.size}")
    private Integer schedulingPoolSize;

}
