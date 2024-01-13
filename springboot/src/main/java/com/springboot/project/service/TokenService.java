package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;

@Service
public class TokenService extends BaseService {

    public void createTokenEntity(String jwtId, String userId) {
        var user = this.UserEntity().where(s -> s.getId().equals(userId)).getOnlyValue();

        var tokenEntity = new TokenEntity();
        tokenEntity.setId(newId());
        tokenEntity.setJwtId(jwtId);
        tokenEntity.setUser(user);
        tokenEntity.setCreateDate(new Date());
        tokenEntity.setUpdateDate(new Date());
        this.persist(tokenEntity);
    }

    public void deleteTokenEntity(String jwtId) {
        var tokenEntity = this.TokenEntity().where(s -> s.getJwtId().equals(jwtId)).getOnlyValue();
        tokenEntity.setUser(null);
        this.remove(tokenEntity);
    }

    public boolean isExistTokenEntity(String jwtId) {
        var exists = this.TokenEntity().where(s -> s.getJwtId().equals(jwtId)).exists();
        return exists;
    }

}
