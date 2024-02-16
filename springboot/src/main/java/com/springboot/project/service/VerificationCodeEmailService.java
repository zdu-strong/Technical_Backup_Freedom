package com.springboot.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.VerificationCodeEmailEntity;
import com.springboot.project.enumerate.VerificationCodeEmailEnum;
import com.springboot.project.model.VerificationCodeEmailModel;

@Service
public class VerificationCodeEmailService extends BaseService {

    public VerificationCodeEmailModel createVerificationCodeEmail(String email) {

        var verificationCodeLength = VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH;

        {
            var beforeCalendar = Calendar.getInstance();
            beforeCalendar.setTime(new Date());
            beforeCalendar.add(Calendar.MONTH, -1);
            Date beforeDate = beforeCalendar.getTime();

            var retryCount = this.VerificationCodeEmailEntity()
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> !s.getHasUsed() || !s.getIsPassed())
                    .count();
            if (retryCount > 1000 && verificationCodeLength < VerificationCodeEmailEnum.MAX_VERIFICATION_CODE_LENGTH) {
                verificationCodeLength = VerificationCodeEmailEnum.MAX_VERIFICATION_CODE_LENGTH;
            }
        }

        {
            var beforeCalendar = Calendar.getInstance();
            beforeCalendar.setTime(new Date());
            beforeCalendar.add(Calendar.DAY_OF_YEAR, -1);
            Date beforeDate = beforeCalendar.getTime();

            var retryCount = this.VerificationCodeEmailEntity()
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> !s.getHasUsed() || !s.getIsPassed())
                    .count();

            if (retryCount > 0
                    && verificationCodeLength < VerificationCodeEmailEnum.MODERATE_VERIFICATION_CODE_LENGTH) {
                verificationCodeLength = VerificationCodeEmailEnum.MODERATE_VERIFICATION_CODE_LENGTH;
            } else if (retryCount == 0) {
                verificationCodeLength = VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH;
            }
        }

        String verificationCode = "";

        for (var i = verificationCodeLength; i > 0; i--) {
            verificationCode += String.valueOf(new BigDecimal(Math.random()).multiply(new BigDecimal(10))
                    .setScale(0, RoundingMode.FLOOR).longValue());
        }

        var verificationCodeEmailEntity = new VerificationCodeEmailEntity();
        verificationCodeEmailEntity.setId(newId());
        verificationCodeEmailEntity.setEmail(email);
        verificationCodeEmailEntity.setVerificationCode(verificationCode);
        verificationCodeEmailEntity.setHasUsed(false);
        verificationCodeEmailEntity.setIsPassed(false);
        verificationCodeEmailEntity.setCreateDate(new Date());
        verificationCodeEmailEntity.setUpdateDate(new Date());
        this.persist(verificationCodeEmailEntity);

        return this.verificationCodeEmailFormatter.format(verificationCodeEmailEntity);
    }

    @SuppressWarnings("resource")
    public boolean isFirstOnTheDurationOfVerificationCodeEmail(String id) {
        var verificationCodeEmailEntity = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        var isFirstOnTheSecond = false;

        {
            var email = verificationCodeEmailEntity.getEmail();
            var createDate = verificationCodeEmailEntity.getCreateDate();

            var timeZone = this.timeZoneUtil.getTimeZoneString("UTC");
            var createDateString = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))
                    .format(createDate);
            Date beforeDate = DateUtils.addSeconds(verificationCodeEmailEntity.getCreateDate(), -1);

            isFirstOnTheSecond = this.VerificationCodeEmailEntity()
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> JPQLFunction
                            .formatDateAsYearMonthDayHourMinuteSecond(s.getCreateDate(), timeZone)
                            .equals(createDateString))
                    .where((s, t) -> !t.stream(VerificationCodeEmailEntity.class)
                            .where(m -> m.getEmail().equals(email))
                            .where(m -> beforeDate.before(m.getCreateDate()))
                            .where(m -> JPQLFunction
                                    .formatDateAsYearMonthDayHourMinuteSecond(m.getCreateDate(), timeZone)
                                    .equals(createDateString))
                            .where(m -> m.getHasUsed())
                            .exists())
                    .sortedBy(s -> s.getId())
                    .sortedBy(s -> s.getCreateDate())
                    .select(s -> s.getId())
                    .findFirst()
                    .filter(s -> s.equals(id))
                    .isPresent();
        }

        if (isFirstOnTheSecond) {
            if (verificationCodeEmailEntity.getVerificationCode()
                    .length() == VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH) {
                var email = verificationCodeEmailEntity.getEmail();
                var createDate = verificationCodeEmailEntity.getCreateDate();

                var timeZone = this.timeZoneUtil.getTimeZoneString("UTC");
                var createDateString = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
                        .format(createDate);
                Date beforeDate = DateUtils.addDays(verificationCodeEmailEntity.getCreateDate(), -1);

                var minVerificationCodeLength = VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH;
                isFirstOnTheSecond = this.VerificationCodeEmailEntity()
                        .where(s -> s.getEmail().equals(email))
                        .where(s -> beforeDate.before(s.getCreateDate()))
                        .where(s -> s.getVerificationCode().length() == minVerificationCodeLength)
                        .where(s -> !s.getHasUsed() || !s.getIsPassed())
                        .where(s -> JPQLFunction
                                .formatDateAsYearMonthDay(s.getCreateDate(), timeZone)
                                .equals(createDateString))
                        .where((s, t) -> !t.stream(VerificationCodeEmailEntity.class)
                                .where(m -> m.getEmail().equals(email))
                                .where(m -> beforeDate.before(m.getCreateDate()))
                                .where(m -> m.getVerificationCode().length() == minVerificationCodeLength)
                                .where(m -> JPQLFunction
                                        .formatDateAsYearMonthDay(m.getCreateDate(), timeZone)
                                        .equals(createDateString))
                                .where(m -> m.getHasUsed() && !m.getIsPassed())
                                .exists())
                        .sortedBy(s -> s.getId())
                        .sortedBy(s -> s.getCreateDate())
                        .select(s -> s.getId())
                        .findFirst()
                        .filter(s -> s.equals(id))
                        .isPresent();
            }
        }

        return isFirstOnTheSecond;
    }

    public void checkVerificationCodeEmailHasBeenUsed(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();

        var verificationCodeEmailEntityOptional = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .findOne();
        if (!verificationCodeEmailEntityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
        }

        var verificationCodeEmailEntity = verificationCodeEmailEntityOptional.get();

        if (!this.isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailEntity.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailEntity.getEmail() + " is wrong");
        }

        if (verificationCodeEmailEntity.getHasUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailEntity.getEmail() + " is wrong");
        }

        if (!verificationCodeEmailEntity.getEmail().equals(verificationCodeEmailModel.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
        }

        {
            var calendar = Calendar.getInstance();
            calendar.setTime(verificationCodeEmailEntity.getCreateDate());
            calendar.add(Calendar.MINUTE, 5);
            Date expiredDate = calendar.getTime();

            if (!verificationCodeEmailEntity.getCreateDate().before(expiredDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
            }

        }

        verificationCodeEmailEntity.setHasUsed(true);
        this.merge(verificationCodeEmailEntity);
    }

    public void checkVerificationCodeEmailIsPassed(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();
        var verificationCodeEmailEntity = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();

        if (!verificationCodeEmailEntity.getVerificationCode()
                .equals(verificationCodeEmailModel.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
        }

        verificationCodeEmailEntity.setIsPassed(true);
        this.merge(verificationCodeEmailEntity);
    }

}
