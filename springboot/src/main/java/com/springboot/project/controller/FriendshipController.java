package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.common.baseController.BaseController;

@RestController
public class FriendshipController extends BaseController {

    @GetMapping("/get_friend_list")
    public ResponseEntity<?> getFriendList(@RequestParam Long pageNum, @RequestParam Long pageSize) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getFriendList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/get_stranger_list")
    public ResponseEntity<?> getStrangerList(@RequestParam Long pageNum, @RequestParam Long pageSize) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getStrangerList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/get_blacklist")
    public ResponseEntity<?> getBlacklist(@RequestParam Long pageNum, @RequestParam Long pageSize) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getBlackList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/get_friend_ship")
    public ResponseEntity<?> getFriendship(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var friendshipModel = this.friendshipService.getFriendship(userId, friendId);
        return ResponseEntity.ok(friendshipModel);
    }

    @PostMapping("/create_friendship")
    public ResponseEntity<?> addFriendshEntity(@RequestParam String friendId, @RequestParam String aesOfUser,
            String aesOfFriend) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        this.friendshipService.createFriendship(userId, friendId, aesOfUser, aesOfFriend);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add_friend")
    public ResponseEntity<?> addFriend(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        this.friendshipService.addToFriendList(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete_from_friend_list")
    public ResponseEntity<?> deleteFromFriendList(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        this.friendshipService.deleteFromFriendList(userId, friendId);
        return ResponseEntity.ok().build();
    }

}
