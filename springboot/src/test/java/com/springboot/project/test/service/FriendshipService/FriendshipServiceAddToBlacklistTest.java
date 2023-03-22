package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.KeyGenerator;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;
import cn.hutool.crypto.asymmetric.KeyType;

public class FriendshipServiceAddToBlacklistTest extends BaseTest {
    private TokenModel user;
    private TokenModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.friendshipService.addToBlacklist(this.user.getUserModel().getId(), this.friend.getUserModel().getId());
        var friendshipModel = this.friendshipService.getFriendship(this.user.getUserModel().getId(),
                this.friend.getUserModel().getId());
        assertEquals(this.user.getUserModel().getId(), friendshipModel.getUser().getId());
        assertEquals(this.friend.getUserModel().getId(), friendshipModel.getFriend().getId());
        assertTrue(friendshipModel.getHasInitiative());
        assertFalse(friendshipModel.getIsFriend());
        assertTrue(friendshipModel.getIsInBlacklist());
        assertFalse(friendshipModel.getIsFriendOfFriend());
        assertFalse(friendshipModel.getIsInBlacklistOfFriend());
        assertTrue(StringUtils.isNotBlank(friendshipModel.getAesOfUser()));
        assertTrue(StringUtils.isBlank(friendshipModel.getAesOfFriend()));
        assertTrue(StringUtils.isNotBlank(this.user.getRSA().decryptStr(
                this.user.getRSA().decryptStr(friendshipModel.getAesOfUser(), KeyType.PrivateKey), KeyType.PublicKey)));
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        var keyOfAES = Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        var aesOfUser = this.user.getRSA().encryptBase64(this.user.getRSA().encryptBase64(keyOfAES, KeyType.PrivateKey), KeyType.PublicKey);
        var aesOfFriend = this.friend.getRSA().encryptBase64(this.user.getRSA().encryptBase64(keyOfAES, KeyType.PrivateKey), KeyType.PublicKey);
        this.friendshipService.createFriendship(this.user.getUserModel().getId(), this.friend.getUserModel().getId(), aesOfUser, aesOfFriend);
    }

}