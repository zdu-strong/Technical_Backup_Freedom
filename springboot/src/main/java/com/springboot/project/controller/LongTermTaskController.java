package com.springboot.project.controller;

import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enumerate.LongTermTaskTempWaitDurationEnum;
import com.springboot.project.model.LongTermTaskModel;

@RestController
public class LongTermTaskController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Because some requests take a long time to execute, so provide this
     * asynchronous task api. Call them first, like this: (
     * this.longTermTaskUtil.run(()->{});). And then call this api for polling to
     * obtain the execution results.
     * 
     * @param id
     * @return
     * @throws InterruptedException
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @GetMapping("/long_term_task")
    public ResponseEntity<?> getLongTermTask(@RequestParam String id)
            throws InterruptedException, JsonMappingException, JsonProcessingException {

        this.longTermTaskService
                .checkIsExistLongTermTaskById(this.encryptDecryptService.decryptByAES(id));

        Calendar calendarOfWait = Calendar.getInstance();
        calendarOfWait.setTime(new Date());
        calendarOfWait.add(Calendar.MILLISECOND,
                Long.valueOf(LongTermTaskTempWaitDurationEnum.TEMP_WAIT_DURATION.toMillis()).intValue());
        Date expireDate = calendarOfWait.getTime();

        while (true) {
            var response = this.longTermTaskService
                    .getLongTermTask(this.encryptDecryptService.decryptByAES(id));
            if (response.getBody() instanceof LongTermTaskModel) {
                var longTermTaskResult = this.objectMapper.readValue(
                        this.objectMapper.writeValueAsString(response.getBody()),
                        new TypeReference<LongTermTaskModel<Object>>() {
                        });
                if (longTermTaskResult.getIsDone() || !new Date().before(expireDate)) {
                    return response;
                } else {
                    Thread.sleep(1);
                }
            } else {
                return response;
            }
        }
    }
}
