package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.LoggerEntity;
import com.springboot.project.model.LoggerModel;

@Service
public class LoggerService extends BaseService {

    public void createLogger(LoggerModel loggerModel) {
        var loggerEntity = new LoggerEntity();
        loggerEntity.setId(Generators.timeBasedGenerator().generate().toString());
        loggerEntity.setCreateDate(new Date());
        loggerEntity.setMessage(loggerModel.getMessage());
        loggerEntity.setLevel(loggerModel.getLevel().toString());
        loggerEntity.setLoggerName(loggerModel.getLoggerName());
        loggerEntity.setHasException(loggerModel.getHasException());
        loggerEntity.setExceptionClassName(loggerModel.getExceptionClassName());
        loggerEntity.setExceptionMessage(loggerModel.getExceptionMessage());
        try {
            loggerEntity.setExceptionStackTrace(
                    new ObjectMapper().writeValueAsString(loggerModel.getExceptionStackTrace()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        loggerEntity.setGitCommitId(loggerModel.getGitCommitId());
        loggerEntity.setGitCommitDate(loggerModel.getGitCommitDate());
        this.entityManager.persist(loggerEntity);
    }

}