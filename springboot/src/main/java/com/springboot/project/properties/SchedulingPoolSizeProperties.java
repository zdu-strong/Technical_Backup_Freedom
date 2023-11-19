package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties
@PropertySource("classpath:application.yml")
public class SchedulingPoolSizeProperties {

    @Autowired
    private Environment environment;

    @Value("${spring.task.scheduling.pool.size}")
    private Integer schedulingPoolSize;

    public Integer getSchedulingPoolSize() {
        return Integer.valueOf(
                this.environment.getProperty("SPRING_TASK_SCHEDULING_POOL_SIZE",
                        String.valueOf(this.schedulingPoolSize)));
    }
}
