package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBlackOrganizeModel {
    
    private String id;
    private Date createDate;
    private Date updateDate;
    private OrganizeModel organize;
    private UserModel user;

}
