package com.springboot.diff;

import lombok.Getter;

@Getter
public enum SupportDatabaseTypeEnum {

    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "CustomMySQLDialect"),

    COCKROACH_DB("cockroachdb", "org.postgresql.Driver", "CustomCockroachdbDialect");

    private String name;
    private String driver;
    private String platform;

    private SupportDatabaseTypeEnum(String name, String driver, String platform) {
        this.name = name;
        this.driver = driver;
        this.platform = platform;
    }

}
