package com.springboot.project.common.baseService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.TimeZoneUtil.TimeZoneUtil;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.entity.*;
import com.springboot.project.format.DistributedExecutionFormatter;
import com.springboot.project.format.FriendshipFormatter;
import com.springboot.project.format.LoggerFormatter;
import com.springboot.project.format.LongTermTaskFormatter;
import com.springboot.project.format.OrganizeFormatter;
import com.springboot.project.format.StorageSpaceFormatter;
import com.springboot.project.format.TokenFormatter;
import com.springboot.project.format.UserBlackOrganizeFormatter;
import com.springboot.project.format.UserEmailFormatter;
import com.springboot.project.format.UserFormatter;
import com.springboot.project.format.UserMessageFormatter;
import com.springboot.project.format.VerificationCodeEmailFormatter;
import com.springboot.project.properties.DateFormatProperties;

@Service
@Transactional(rollbackFor = Throwable.class)
public abstract class BaseService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    protected Storage storage;

    @Autowired
    protected TimeZoneUtil timeZoneUtil;

    @Autowired
    protected DateFormatProperties dateFormatProperties;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected StorageSpaceFormatter storageSpaceFormatter;

    @Autowired
    protected UserEmailFormatter userEmailFormatter;

    @Autowired
    protected UserFormatter userFormatter;

    @Autowired
    protected LongTermTaskFormatter longTermTaskFormatter;

    @Autowired
    protected OrganizeFormatter organizeFormatter;

    @Autowired
    protected UserMessageFormatter userMessageFormatter;

    @Autowired
    protected FriendshipFormatter friendshipFormatter;

    @Autowired
    protected LoggerFormatter loggerFormatter;

    @Autowired
    protected VerificationCodeEmailFormatter verificationCodeEmailFormatter;

    @Autowired
    protected DistributedExecutionFormatter distributedExecutionFormatter;

    @Autowired
    protected UserBlackOrganizeFormatter userBlackOrganizeFormatter;

    @Autowired
    protected TokenFormatter tokenFormatter;

    protected void persist(Object entity) {
        this.entityManager.persist(entity);
    }

    protected void merge(Object entity) {
        this.entityManager.merge(entity);
    }

    protected void remove(Object entity) {
        this.entityManager.remove(entity);
    }

    protected JPAJinqStream<StorageSpaceEntity> StorageSpaceEntity() {
        return this.streamAll(StorageSpaceEntity.class);
    }

    protected JPAJinqStream<EncryptDecryptEntity> EncryptDecryptEntity() {
        return this.streamAll(EncryptDecryptEntity.class);
    }

    protected JPAJinqStream<UserEmailEntity> UserEmailEntity() {
        return this.streamAll(UserEmailEntity.class);
    }

    protected JPAJinqStream<UserEntity> UserEntity() {
        return this.streamAll(UserEntity.class);
    }

    protected JPAJinqStream<LongTermTaskEntity> LongTermTaskEntity() {
        return this.streamAll(LongTermTaskEntity.class);
    }

    protected JPAJinqStream<OrganizeEntity> OrganizeEntity() {
        return this.streamAll(OrganizeEntity.class);
    }

    protected JPAJinqStream<UserMessageEntity> UserMessageEntity() {
        return this.streamAll(UserMessageEntity.class);
    }

    protected JPAJinqStream<TokenEntity> TokenEntity() {
        return this.streamAll(TokenEntity.class);
    }

    protected JPAJinqStream<FriendshipEntity> FriendshipEntity() {
        return this.streamAll(FriendshipEntity.class);
    }

    protected JPAJinqStream<LoggerEntity> LoggerEntity() {
        return this.streamAll(LoggerEntity.class);
    }

    protected JPAJinqStream<VerificationCodeEmailEntity> VerificationCodeEmailEntity() {
        return this.streamAll(VerificationCodeEmailEntity.class);
    }

    protected JPAJinqStream<DistributedExecutionEntity> DistributedExecutionEntity() {
        return this.streamAll(DistributedExecutionEntity.class);
    }

    protected JPAJinqStream<OrganizeClosureEntity> OrganizeClosureEntity() {
        return this.streamAll(OrganizeClosureEntity.class);
    }

    protected JPAJinqStream<UserBlackOrganizeEntity> UserBlackOrganizeEntity() {
        return this.streamAll(UserBlackOrganizeEntity.class);
    }

    protected JPAJinqStream<UserBlackOrganizeClosureEntity> UserBlackOrganizeClosureEntity() {
        return this.streamAll(UserBlackOrganizeClosureEntity.class);
    }

    private <U> JPAJinqStream<U> streamAll(Class<U> entity) {
        var jinqJPAStreamProvider = new JinqJPAStreamProvider(
                entityManager.getMetamodel());
        JPQLFunction.registerCustomSqlFunction(jinqJPAStreamProvider);
        jinqJPAStreamProvider.setHint("exceptionOnTranslationFail", true);
        return jinqJPAStreamProvider.streamAll(entityManager, entity);
    }

    protected String newId() {
        return Generators.timeBasedReorderedGenerator().generate().toString();
    }

}