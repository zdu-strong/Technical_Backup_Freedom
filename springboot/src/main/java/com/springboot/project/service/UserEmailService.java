package com.springboot.project.service;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEmailEntity;

@Service
public class UserEmailService extends BaseService {

    public void createUserEmail(String email, String userId) {
        var userEntity = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .where(s -> !s.getIsDeleted())
                .getOnlyValue();
        UserEmailEntity userEmailEntity = new UserEmailEntity();
        userEmailEntity.setId(newId());
        userEmailEntity.setEmail(email);
        userEmailEntity.setUser(userEntity);
        userEmailEntity.setCreateDate(new Date());
        userEmailEntity.setUpdateDate(new Date());
        userEmailEntity.setIsDeleted(false);
        userEmailEntity.setDeletedKey("");

        this.persist(userEmailEntity);
    }

    public void checkEmailIsNotUsed(String email) {
        var isPresent = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> !s.getIsDeleted())
                .exists();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail " + email + " has bound account");
        }
    }
}
