package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
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
        var verificationCodeEmailModel = new VerificationCodeEmailModel();
        BeanUtils.copyProperties(verificationCodeEmailEntity, verificationCodeEmailModel);
        verificationCodeEmailModel.setVerificationCodeLength(
                Integer.valueOf(verificationCodeEmailEntity.getVerificationCode().length()).longValue());
        if (!this.isTestOrDevModeProperties.getIsTestOrDevMode()) {
            verificationCodeEmailModel.setVerificationCode(null);
        }
        return verificationCodeEmailModel;
    }
}
