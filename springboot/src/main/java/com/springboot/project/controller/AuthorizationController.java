package com.springboot.project.controller;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.VerificationCodeEmailModel;

@RestController
public class AuthorizationController extends BaseController {

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        if (StringUtils.isBlank(userModel.getPublicKeyOfRSA())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please set the public key of RSA");
        }

        if (StringUtils.isBlank(userModel.getPrivateKeyOfRSA())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please set the public key of RSA");
        }

        if (StringUtils.isBlank(userModel.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in nickname");
        }

        if (userModel.getUsername().trim().length() != userModel.getUsername().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }

        {
            for (var userEmail : userModel.getUserEmailList()) {
                if (StringUtils.isBlank(userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
                }

                if (!Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
                }

                if (StringUtils.isBlank(userEmail.getVerificationCodeEmail().getVerificationCode())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The verification code of email " + userEmail.getEmail() + " cannot be empty");
                }

                userEmail.getVerificationCodeEmail().setEmail(userEmail.getEmail());

                this.verificationCodeEmailService
                        .checkVerificationCodeEmailHasBeenUsed(userEmail.getVerificationCodeEmail());

                this.verificationCodeEmailService
                        .checkVerificationCodeEmailIsPassed(userEmail.getVerificationCodeEmail());

                this.userEmailService.checkEmailIsNotUsed(userEmail.getEmail());
            }
        }

        var user = this.userService.signUp(userModel);

        var accessToken = this.tokenUtil.generateAccessToken(user.getId());

        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/sign_in")
    public ResponseEntity<?> signIn(@RequestParam String userId, @RequestParam String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException, JsonMappingException, JsonProcessingException {
        var user = this.userService.getUserWithMoreInformation(userId);

        {
            var publicKeyOfRSA = JinqStream
                    .from(this.objectMapper.readValue(
                            new String(Base64.getDecoder().decode(user.getPassword()), StandardCharsets.UTF_8),
                            new TypeReference<List<String>>() {
                            }))
                    .skip(1).getOnlyValue();
            var passwordString = this.encryptDecryptService.decryptByByPublicKeyOfRSA(password,
                    publicKeyOfRSA);
            var userModel = this.objectMapper.readValue(passwordString, UserModel.class);
            var createDate = userModel.getCreateDate();

            var minCalendar = Calendar.getInstance();
            minCalendar.setTime(createDate);
            minCalendar.add(Calendar.MINUTE, -5);
            var minDate = minCalendar.getTime();
            var maxCalendar = Calendar.getInstance();
            maxCalendar.setTime(createDate);
            maxCalendar.add(Calendar.MINUTE, 5);
            var maxDate = maxCalendar.getTime();
            if (createDate.before(minDate) || createDate.after(maxDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
            }
        }

        var accessToken = this.tokenUtil.generateAccessToken(userId);

        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/sign_out")
    public ResponseEntity<?> signOut() {
        if (this.permissionUtil.isSignIn(request)) {
            var jwtId = this.tokenUtil.getDecodedJWTOfAccessToken(this.tokenUtil.getAccessToken(request)).getId();
            if (this.tokenService.isExistTokenEntity(jwtId)) {
                this.tokenService.deleteTokenEntity(jwtId);
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_user_info")
    public ResponseEntity<?> getUserInfo() {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign_in/get_account")
    public ResponseEntity<?> getAccountForSignIn(@RequestParam String userId)
            throws JsonMappingException, JsonProcessingException {

        if (StringUtils.isBlank(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in the account");
        }

        this.userService.checkExistAccount(userId);

        var userModel = this.userService.getUserWithMoreInformation(userId);
        var user = new UserModel();
        user.setId(userModel.getId());
        user.setPassword(JinqStream
                .from(this.objectMapper.readValue(
                        new String(Base64.getDecoder().decode(userModel.getPassword()), StandardCharsets.UTF_8),
                        new TypeReference<List<String>>() {
                        }))
                .limit(1).getOnlyValue());

        return ResponseEntity.ok(user);
    }

    @PostMapping("/email/send_verification_code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email)
            throws InvalidKeySpecException, NoSuchAlgorithmException, InterruptedException, ParseException {

        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
        }

        if (!Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
        }

        VerificationCodeEmailModel verificationCodeEmailModel = null;
        for (var i = 10; i > 0; i--) {
            var verificationCodeEmailModelTwo = this.verificationCodeEmailService.createVerificationCodeEmail(email);

            var timeZone = TimeZone.getTimeZone(this.timeZoneUtil.getTimeZoneFromUTC());
            var calendar = Calendar.getInstance();
            calendar.setTimeZone(timeZone);
            calendar.setTime(verificationCodeEmailModelTwo.getCreateDate());
            calendar.add(Calendar.SECOND, 1);
            var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(timeZone);
            var createDate = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
            Thread.sleep(createDate.getTime() - verificationCodeEmailModelTwo.getCreateDate().getTime());

            if (this.verificationCodeEmailService
                    .isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailModelTwo.getId())) {
                verificationCodeEmailModel = verificationCodeEmailModelTwo;
                break;
            }
        }

        if (verificationCodeEmailModel == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Too many verification code requests in a short period of time");
        }

        this.authorizationEmailUtil.sendVerificationCode(email, verificationCodeEmailModel.getVerificationCode());

        return ResponseEntity.ok(verificationCodeEmailModel);
    }

    @GetMapping("/sign_in/alipay/generate_qr_code")
    public ResponseEntity<?> generateQrCode() throws Throwable {
        var url = new URIBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm")
                .setParameter("app_id", "2021002177648626").setParameter("scope", "auth_user")
                .setParameter("redirect_uri", "https://kame-sennin.com/abc").setParameter("state", "init").build();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url.toString(), BarcodeFormat.QR_CODE, 200, 200);
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            String imageData = Base64.getEncoder().encodeToString(pngData);
            var imageUrl = "data:image/png;base64," + imageData;
            return ResponseEntity.ok(imageUrl);
        }
    }

}
