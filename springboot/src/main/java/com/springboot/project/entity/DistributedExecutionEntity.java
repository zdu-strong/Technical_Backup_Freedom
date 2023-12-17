package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "version", "pageSize", "pageNum" }) })
@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private Long pageNum;

    @Column(nullable = false)
    private Long pageSize;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;
}
