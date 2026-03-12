package com.revshop.userservice.controller;

import com.revshop.userservice.dto.RegisterRequest;
import com.revshop.userservice.dto.LoginRequest;
import com.revshop.userservice.dto.LoginResponse;
import com.revshop.userservice.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String,String> request){
        return ResponseEntity.ok(
                authService.forgotPassword(request.get("email"))
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String,String> request){
        return ResponseEntity.ok(
                authService.verifyOtp(
                        request.get("email"),
                        request.get("otp")
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String,String> request){
        return ResponseEntity.ok(
                authService.resetPassword(
                        request.get("email"),
                        request.get("newPassword")
                )
        );
    }

}

