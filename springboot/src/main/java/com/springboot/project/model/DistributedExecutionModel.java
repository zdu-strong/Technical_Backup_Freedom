package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionModel {

    private String id;
    private String name;
    private String version;
    private Long pageSize;
    private Long pageNum;
    private Date createDate;
    private Date updateDate;
}
