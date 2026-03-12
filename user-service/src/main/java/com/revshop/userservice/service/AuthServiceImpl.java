package com.revshop.userservice.service;
import org.springframework.transaction.annotation.Transactional;

import com.revshop.userservice.dto.LoginResponse;
import com.revshop.userservice.dto.RegisterRequest;
import com.revshop.userservice.exception.InvalidCredentialsException;
import com.revshop.userservice.model.Address;
import com.revshop.userservice.model.OTP;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.model.User;
import com.revshop.userservice.repository.AddressRepository;
import com.revshop.userservice.repository.PasswordOtpRepository;
import com.revshop.userservice.repository.UserRepository;
import com.revshop.userservice.security.JwtUtil;
import com.revshop.userservice.service.AuthService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger =
            LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final JwtUtil jwtUtil;
    private final PasswordOtpRepository passwordOtpRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AddressRepository addressRepository,
                           JwtUtil jwtUtil,
                           PasswordOtpRepository passwordOtpRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.jwtUtil = jwtUtil;
        this.passwordOtpRepository = passwordOtpRepository;
    }

    @Override
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Validate role
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new RuntimeException("Role is required");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Must be BUYER or SELLER");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setBusinessName(request.getBusinessName());
        user.setRole(role);

        User savedUser = userRepository.save(user);

        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setUser(savedUser);

        addressRepository.save(address);

        return "User registered successfully!";
    }


    @Override
    public LoginResponse login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId(),
                user.getName()
        );

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );
    }


    @Override
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        OTP passwordOtp = new OTP();
        passwordOtp.setEmail(email);
        passwordOtp.setOtp(otp);
        passwordOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        passwordOtpRepository.save(passwordOtp);

        logger.info("OTP for {} is {}", email, otp);

        return otp;
    }

    @Override
    public String verifyOtp(String email, String otp) {

        OTP storedOtp = passwordOtpRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!storedOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (storedOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        passwordOtpRepository.delete(storedOtp);

        return "OTP verified successfully";
    }

    @Override
    public String resetPassword(String email, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete OTP after successful reset
        passwordOtpRepository.deleteByEmail(email);

        logger.info("Password reset successfully for {}", email);

        return "Password reset successfully";
    }
}

