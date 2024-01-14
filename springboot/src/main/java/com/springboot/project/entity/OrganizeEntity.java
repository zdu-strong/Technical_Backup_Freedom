package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "parentId", "name", "deletedKey" })
}, indexes = {
        @Index(columnList = "id, isDeleted", unique = true)
})
public class OrganizeEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private String deletedKey;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = true)
    private OrganizeEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrganizeEntity> childList;

    @OneToOne(mappedBy = "organize", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = true)
    private OrganizeClosureEntity organizeClosure;

    @OneToMany(mappedBy = "organize", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserBlackOrganizeEntity> userBlackOrganizeList;

    @OneToMany(mappedBy = "organize", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserBlackOrganizeClosureEntity> userBlackOrganizeClosureList;

}
