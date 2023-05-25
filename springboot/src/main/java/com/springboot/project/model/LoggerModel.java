package com.springboot.project.model;

import java.util.Date;
import java.util.List;
import ch.qos.logback.classic.Level;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LoggerModel {

    private String id;

    private String message;

    private Date createDate;

    private Level level;

    private Boolean hasException;

    private String loggerName;

    private String exceptionClassName;

    private String exceptionMessage;

    private List<String> exceptionStackTrace;

    private String gitCommitId;

    private Date gitCommitDate;

}