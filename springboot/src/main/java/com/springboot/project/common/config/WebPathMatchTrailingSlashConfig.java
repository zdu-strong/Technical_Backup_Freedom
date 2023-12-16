package com.springboot.project.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebPathMatchTrailingSlashConfig {

    @Autowired
    private TrailingSlashRedirectFilter trailingSlashRedirectFilter;

    @Bean
    public FilterRegistrationBean<Filter> trailingSlashFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(trailingSlashRedirectFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}