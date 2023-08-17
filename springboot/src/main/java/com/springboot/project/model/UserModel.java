package com.springboot.project.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserModel {
    private String id;
    private String username;
    private String email;
    private String publicKeyOfRSA;
    private String privateKeyOfRSA;
    private Date createDate;
    private Date updateDate;
    private List<UserEmailModel> userEmailList;
}
