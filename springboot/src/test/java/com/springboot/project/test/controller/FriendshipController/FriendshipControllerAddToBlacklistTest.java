package com.springboot.project.test.controller.FriendshipController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.FriendshipModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class FriendshipControllerAddToBlacklistTest extends BaseTest {

    private UserModel user;
    private UserModel friend;
    private String aesOfUser;
    private String aesOfFriend;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder("/friendship/add_to_blacklist")
                .setParameter("friendId", this.friend.getId())
                .setParameter("aesOfUser", this.aesOfUser)
                .setParameter("aesOfFriend", this.aesOfFriend)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, FriendshipModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getId()));
        assertEquals(this.aesOfUser, response.getBody().getAesOfUser());
        assertNull(response.getBody().getAesOfFriend());
        assertEquals(this.user.getId(), response.getBody().getUser().getId());
        assertEquals(this.friend.getId(), response.getBody().getFriend().getId());
        assertNotNull(response.getBody().getCreateDate());
        assertNotNull(response.getBody().getUpdateDate());
        assertTrue(StringUtils.isNotBlank(response.getBody().getFriend().getId()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getFriend().getUsername()));
        assertEquals(this.friend.getUsername(), response.getBody().getFriend().getUsername());
        assertNotNull(response.getBody().getFriend().getCreateDate());
        assertNotNull(response.getBody().getFriend().getUpdateDate());
        assertTrue(StringUtils.isNotBlank(response.getBody().getFriend().getPublicKeyOfRSA()));
        assertTrue(response.getBody().getHasInitiative());
        assertFalse(response.getBody().getIsFriend());
        assertFalse(response.getBody().getIsFriendOfFriend());
        assertTrue(response.getBody().getIsInBlacklist());
        assertFalse(response.getBody().getIsInBlacklistOfFriend());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.friend = this.createAccount(friendEmail);
        this.user = this.createAccount(userEmail);
        var keyOfAES = this.encryptDecryptService.generateSecretKeyOfAES();
        this.aesOfUser = this.encryptDecryptService.encryptByPublicKeyOfRSA(
                this.encryptDecryptService.encryptByPrivateKeyOfRSA(keyOfAES, this.user.getPrivateKeyOfRSA()),
                this.user.getPublicKeyOfRSA());
        this.aesOfFriend = this.encryptDecryptService.encryptByPublicKeyOfRSA(
                this.encryptDecryptService.encryptByPrivateKeyOfRSA(keyOfAES, this.user.getPrivateKeyOfRSA()),
                this.friend.getPublicKeyOfRSA());
    }

}
