package com.springboot.project.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;

@Service
public class UserService extends BaseService {

    @Autowired
    private UserEmailService userEmailService;

    public UserModel signUp(UserModel userModel) {

        var userEntity = new UserEntity();
        userEntity.setId(newId());
        userEntity.setUsername(userModel.getUsername());
        userEntity.setPrivateKeyOfRSA(userModel.getPrivateKeyOfRSA());
        userEntity.setPublicKeyOfRSA(userModel.getPublicKeyOfRSA());
        userEntity.setPassword(userModel.getPassword());
        userEntity.setIsDeleted(false);
        userEntity.setCreateDate(new Date());
        userEntity.setUpdateDate(new Date());
        this.persist(userEntity);

        for (var userEmail : userModel.getUserEmailList()) {
            this.userEmailService.createUserEmail(userEmail.getEmail(), userEntity.getId());
        }

        return this.userFormatter.format(userEntity);
    }

    public UserModel getUserWithMoreInformation(String userIdOrEmail) {
        var userOptional = this.UserEntity().where(s -> s.getId().equals(userIdOrEmail))
                .where(s -> !s.getIsDeleted())
                .findOne();
        if (userOptional.isPresent()) {
            return this.userFormatter.formatWithMoreInformation(userOptional.get());
        }

        var user = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(userIdOrEmail))
                .where(s -> !s.getIsDeleted())
                .where(s -> !s.getUser().getIsDeleted())
                .select(s -> s.getUser())
                .getOnlyValue();

        return this.userFormatter.formatWithMoreInformation(user);
    }

    public UserModel getUserById(String id) {
        var user = this.UserEntity().where(s -> s.getId().equals(id)).where(s -> !s.getIsDeleted())
                .getOnlyValue();
        return this.userFormatter.format(user);
    }

    public void checkExistAccount(String userIdOrEmail) {
        var stream = this.UserEntity().leftOuterJoinList(s -> s.getUserEmailList())
                .where(s -> s.getOne().getId().equals(userIdOrEmail)
                        || (s.getTwo().getEmail().equals(userIdOrEmail) && !s.getTwo().getIsDeleted()))
                .where(s -> !s.getOne().getIsDeleted())
                .group(s -> s.getOne().getId(), (s, t) -> t.count());
        if (!stream.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exist");
        }
    }

}
