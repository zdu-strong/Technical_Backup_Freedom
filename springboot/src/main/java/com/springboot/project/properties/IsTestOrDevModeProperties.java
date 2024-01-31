package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class IsTestOrDevModeProperties {

    @Value("${properties.is.test.or.dev.mode}")
    private Boolean isTestOrDevMode;

}

