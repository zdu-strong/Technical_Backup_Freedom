package com.springboot.project.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.properties.IsTestOrDevModeProperties;

@Service
public class VerificationCodeEmailFormatter extends BaseService {

    @Autowired
    private IsTestOrDevModeProperties isTestOrDevModeProperties;

    public VerificationCodeEmailModel format(VerificationCodeEmailEntity verificationCodeEmailEntity) {
        var verificationCodeEmailModel = new VerificationCodeEmailModel()
                .setId(verificationCodeEmailEntity.getId())
                .setEmail(verificationCodeEmailEntity.getEmail())
                .setVerificationCode(verificationCodeEmailEntity.getVerificationCode())
                .setVerificationCodeLength(Integer.valueOf(verificationCodeEmailEntity.getVerificationCode().length()).longValue())
                .setHasUsed(verificationCodeEmailEntity.getHasUsed())
                .setIsPassed(verificationCodeEmailEntity.getIsPassed())
                .setCreateDate(verificationCodeEmailEntity.getCreateDate())
                .setUpdateDate(verificationCodeEmailEntity.getUpdateDate());

        if(!this.isTestOrDevModeProperties.getIsTestOrDevMode()){
            verificationCodeEmailModel.setVerificationCode(null);
        }

        return verificationCodeEmailModel;
    }
}
