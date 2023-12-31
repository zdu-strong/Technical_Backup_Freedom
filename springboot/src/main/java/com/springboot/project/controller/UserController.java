package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends BaseController {

    @GetMapping("/user")
    public ResponseEntity<?> getUserById(@RequestParam String id) throws IOException {
        var userModel = this.userService.getUserById(id);
        return ResponseEntity.ok(userModel);
    }

}
