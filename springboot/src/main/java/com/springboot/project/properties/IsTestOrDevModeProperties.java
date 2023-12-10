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
public class IsTestOrDevModeProperties {

    @Autowired
    private Environment environment;

    @Value("${properties.is.test.or.dev.mode}")
    private Boolean isTestOrDevMode;

    public Boolean getIsTestOrDevMode() {
        return Boolean.valueOf(
                this.environment.getProperty("PROPERTIES_IS_TEST_OR_DEF_MODE",
                        String.valueOf(this.isTestOrDevMode)));
    }
}

