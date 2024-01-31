package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class SchedulingPoolSizeProperties {

    @Value("${spring.task.scheduling.pool.size}")
    private Long schedulingPoolSize;

}
