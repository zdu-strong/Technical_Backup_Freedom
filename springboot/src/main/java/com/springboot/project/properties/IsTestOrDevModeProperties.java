package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class IsTestOrDevModeProperties {

    @Value("${properties.is.test.or.dev.mode}")
    private Boolean isTestOrDevMode;

}

