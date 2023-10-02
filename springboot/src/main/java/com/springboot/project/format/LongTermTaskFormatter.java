package com.springboot.project.format;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.service.BaseService;

@Service
public class LongTermTaskFormatter extends BaseService {
    private Duration tempTaskSurvivalDuration = Duration.ofMinutes(1);

    public String formatThrowable(Throwable e) {
        try {
            var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            var text = new ObjectMapper()
                    .setDateFormat(simpleDateFormat)
                    .configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true)
                    .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false).writeValueAsString(e);
            return text;
        } catch (JsonProcessingException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }
    }

    public ResponseEntity<?> format(LongTermTaskEntity longTermTaskEntity) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MILLISECOND, Long.valueOf(0 - this.tempTaskSurvivalDuration.toMillis()).intValue());
            Date expireDate = calendar.getTime();
            if (!longTermTaskEntity.getIsDone() && longTermTaskEntity.getUpdateDate().before(expireDate)) {
                throw new RuntimeException("The task failed because it stopped");
            }

            var longTermTaskModel = new LongTermTaskModel<Object>().setId(longTermTaskEntity.getId())
                    .setCreateDate(longTermTaskEntity.getCreateDate()).setUpdateDate(longTermTaskEntity.getUpdateDate())
                    .setIsDone(longTermTaskEntity.getIsDone());

            if (longTermTaskEntity.getIsDone()) {
                var result = this.objectMapper.readTree(longTermTaskEntity.getResult());
                longTermTaskModel.setResult(this.objectMapper
                        .readValue(this.objectMapper.writeValueAsString(result.get("body")), Object.class));
                HttpHeaders httpHeaders = new HttpHeaders();
                result.get("headers").fields().forEachRemaining((s) -> {
                    try {
                        httpHeaders.addAll(s.getKey(),
                                this.objectMapper.readValue(this.objectMapper.writeValueAsString(s.getValue()),
                                        new TypeReference<List<String>>() {
                                        }));
                    } catch (JsonMappingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });
                if (String.valueOf(result.get("statusCodeValue").asInt()).startsWith("2")) {
                    ResponseEntity<LongTermTaskModel<?>> response = ResponseEntity
                            .status(result.get("statusCodeValue").asInt())
                            .headers(httpHeaders).body(longTermTaskModel);
                    return response;
                } else {
                    ResponseEntity<?> response = ResponseEntity
                            .status(result.get("statusCodeValue").asInt())
                            .headers(httpHeaders).body(result.get("body"));
                    return response;
                }
            } else {
                return ResponseEntity.ok().body(longTermTaskModel);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
