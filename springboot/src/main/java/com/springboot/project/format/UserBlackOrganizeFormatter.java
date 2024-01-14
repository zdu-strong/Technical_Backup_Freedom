package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.UserBlackOrganizeModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.entity.*;

@Service
public class UserBlackOrganizeFormatter extends BaseService {

    public UserBlackOrganizeModel format(UserBlackOrganizeEntity userBlackOrganizeEntity) {
        var userBlackOrganizeModel = new UserBlackOrganizeModel();
        userBlackOrganizeModel.setId(userBlackOrganizeEntity.getId());
        userBlackOrganizeModel.setCreateDate(userBlackOrganizeModel.getCreateDate());
        userBlackOrganizeModel.setUpdateDate(userBlackOrganizeModel.getUpdateDate());
        userBlackOrganizeModel.setUser(new UserModel().setId(userBlackOrganizeModel.getUser().getId()));
        userBlackOrganizeModel.setOrganize(new OrganizeModel().setId(userBlackOrganizeModel.getOrganize().getId()));
        return userBlackOrganizeModel;
    }

}
