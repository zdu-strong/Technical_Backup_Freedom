package com.springboot.project.format;

import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.FriendshipModel;
import com.springboot.project.model.UserModel;

@Service
public class FriendshipFormatter extends BaseService {

    public FriendshipModel format(FriendshipEntity friendshipEntity) {
        var userId = friendshipEntity.getUser().getId();
        var friendId = friendshipEntity.getFriend().getId();
        var friendshipEntityOfFriend = this.FriendshipEntity().where(s -> s.getUser().getId().equals(friendId))
                .where(s -> s.getFriend().getId().equals(userId)).getOnlyValue();
        var friendshipModel = new FriendshipModel().setId(friendshipEntity.getId())
                .setIsInBlacklist(friendshipEntity.getIsInBlacklist())
                .setIsFriend(!friendshipEntity.getIsInBlacklist() && friendshipEntity.getIsFriend())
                .setCreateDate(friendshipEntity.getCreateDate())
                .setUpdateDate(friendshipEntity.getUpdateDate())
                .setUser(new UserModel().setId(friendshipEntity.getUser().getId()))
                .setFriend(this.userFormatter.format(friendshipEntity.getFriend()))
                .setHasInitiative(friendshipEntity.getHasInitiative())
                .setAesOfUser(friendshipEntity.getSecretKeyOfAES())
                .setIsFriendOfFriend(
                        !friendshipEntityOfFriend.getIsInBlacklist() && friendshipEntityOfFriend.getIsFriend())
                .setIsInBlacklistOfFriend(friendshipEntityOfFriend.getIsInBlacklist());
        return friendshipModel;
    }

    public FriendshipModel format(FriendshipEntity friendshipEntity, UserEntity userEntity, UserEntity friendEntity) {
        if (friendshipEntity != null) {
            return this.format(friendshipEntity);
        }
        var friendshipModel = new FriendshipModel().setIsInBlacklist(false)
                .setIsFriend(false).setUser(new UserModel().setId(userEntity.getId()))
                .setFriend(this.userFormatter.format(friendEntity));
        return friendshipModel;
    }
}
