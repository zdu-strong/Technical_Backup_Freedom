package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class FriendshipServiceGetFriendshipTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var friendshipModel = this.friendshipService.getFriendship(this.user.getId(),
                this.friend.getId());
        assertEquals(this.user.getId(), friendshipModel.getUser().getId());
        assertEquals(this.friend.getId(), friendshipModel.getFriend().getId());
        assertTrue(friendshipModel.getHasInitiative());
        assertFalse(friendshipModel.getIsFriend());
        assertFalse(friendshipModel.getIsInBlacklist());
        assertFalse(friendshipModel.getIsFriendOfFriend());
        assertFalse(friendshipModel.getIsInBlacklistOfFriend());
        assertTrue(StringUtils.isNotBlank(friendshipModel.getAesOfUser()));
        assertTrue(StringUtils.isBlank(friendshipModel.getAesOfFriend()));
        assertTrue(
                StringUtils
                        .isNotBlank(
                                this.encryptDecryptService.decryptByByPublicKeyOfRSA(
                                        this.encryptDecryptService.decryptByByPrivateKeyOfRSA(
                                                friendshipModel.getAesOfUser(), this.user.getPrivateKeyOfRSA()),
                                        this.user.getPublicKeyOfRSA())));
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException {
        var userEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
        var keyOfAES = this.encryptDecryptService.generateSecretKeyOfAES();
        var aesOfUser = this.encryptDecryptService.encryptByPublicKeyOfRSA(
                this.encryptDecryptService.encryptByPrivateKeyOfRSA(keyOfAES, this.user.getPrivateKeyOfRSA()),
                this.user.getPublicKeyOfRSA());
        var aesOfFriend = this.encryptDecryptService.encryptByPublicKeyOfRSA(
                this.encryptDecryptService.encryptByPrivateKeyOfRSA(keyOfAES, this.user.getPrivateKeyOfRSA()),
                this.friend.getPublicKeyOfRSA());
        this.friendshipService.createFriendship(this.user.getId(), this.friend.getId(), aesOfUser, aesOfFriend);
    }

}
