package com.revshop.userservice.service;

import com.revshop.userservice.dto.RegisterRequest;
import com.revshop.userservice.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(String email, String password);

    String register(RegisterRequest request);

    String forgotPassword(String email);

    String verifyOtp(String email, String otp);

    String resetPassword(String email, String newPassword);
}

