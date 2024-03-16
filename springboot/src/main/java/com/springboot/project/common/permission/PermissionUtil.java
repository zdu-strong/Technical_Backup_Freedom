package com.springboot.project.common.permission;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.service.TokenService;

@Component
public class PermissionUtil {

    @Autowired
    private TokenService tokenService;

    public void checkIsSignIn(HttpServletRequest request) {
        if(!this.isSignIn(request)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
    }

    public void checkIsSignIn(String accessToken) {
        if(!this.isSignIn(accessToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
    }

    public boolean isSignIn(HttpServletRequest request) {
        String accessToken = this.tokenService.getAccessToken(request);
        return this.isSignIn(accessToken);
    }

    public boolean isSignIn(String accessToken) {
        try {
            this.tokenService.getDecodedJWTOfAccessToken(accessToken);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public String getUserId(HttpServletRequest request) {
        String accessToken = this.tokenService.getAccessToken(request);
        return this.getUserId(accessToken);
    }

    public String getUserId(String accessToken) {
        return this.tokenService.getDecodedJWTOfAccessToken(accessToken).getSubject();
    }
}
