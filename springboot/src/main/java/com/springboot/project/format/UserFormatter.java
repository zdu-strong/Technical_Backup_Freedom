package com.springboot.project.format;

import org.springframework.stereotype.Service;

import com.beust.jcommander.internal.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;

@Service
public class UserFormatter extends BaseService {

    public UserModel format(UserEntity userEntity) {
        var userModel = new UserModel()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setPublicKeyOfRSA(userEntity.getPublicKeyOfRSA())
                .setCreateDate(userEntity.getCreateDate())
                .setUpdateDate(userEntity.getUpdateDate())
                .setUserEmailList(Lists.newArrayList());
        return userModel;
    }

    public UserModel formatWithMoreInformation(UserEntity userEntity) {
        var userModel = this.format(userEntity);
        userModel.setPrivateKeyOfRSA(userEntity.getPrivateKeyOfRSA());
        userModel.setPassword(userEntity.getPassword());
        var userId = userEntity.getId();
        var userEmailList = this.UserEmailEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> !s.getIsDeleted())
                .map(s -> this.userEmailFormatter.format(s))
                .toList();
        userModel.setUserEmailList(userEmailList);
        return userModel;
    }

}
