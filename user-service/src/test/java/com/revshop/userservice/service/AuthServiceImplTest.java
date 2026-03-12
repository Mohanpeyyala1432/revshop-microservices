package com.revshop.userservice.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordOtpRepository passwordOtpRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@email.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("1234567890");
        registerRequest.setBusinessName("Test Business");
        registerRequest.setRole("BUYER");
        registerRequest.setStreet("123 Test St");
        registerRequest.setCity("Test City");
        registerRequest.setState("Test State");
        registerRequest.setPincode("123456");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.BUYER);
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        String result = authService.register(registerRequest);

        // Assert
        assertEquals("User registered successfully!", result);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("test@email.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.BUYER, savedUser.getRole());

        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Email already registered!", exception.getMessage());
        
        verify(userRepository, never()).save(any(User.class));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void register_InvalidRole_ThrowsException() {
        // Arrange
        registerRequest.setRole("INVALID_ROLE");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Invalid role. Must be BUYER or SELLER", exception.getMessage());
    }

    @Test
    void login_Success() {
        // Arrange
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong(), anyString())).thenReturn("testToken");

        // Act
        LoginResponse response = authService.login("test@email.com", "password123");

        // Assert
        assertNotNull(response);
        assertEquals("testToken", response.getToken());
        assertEquals("test@email.com", response.getEmail());
        assertEquals("BUYER", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("wrong@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login("wrong@email.com", "password123"));
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login("test@email.com", "wrongpassword"));
    }

    @Test
    void forgotPassword_Success() {
        // Arrange
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        // Act
        String otp = authService.forgotPassword("test@email.com");

        // Assert
        assertNotNull(otp);
        assertTrue(otp.length() == 6);
        verify(passwordOtpRepository).save(any(OTP.class));
    }

    @Test
    void verifyOtp_Success() {
        // Arrange
        OTP otp = new OTP();
        otp.setOtp("123456");
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        
        when(passwordOtpRepository.findTopByEmailOrderByIdDesc("test@email.com")).thenReturn(Optional.of(otp));

        // Act
        String result = authService.verifyOtp("test@email.com", "123456");

        // Assert
        assertEquals("OTP verified successfully", result);
        verify(passwordOtpRepository).delete(otp);
    }

    @Test
    void verifyOtp_Expired_ThrowsException() {
        // Arrange
        OTP otp = new OTP();
        otp.setOtp("123456");
        otp.setExpiryTime(LocalDateTime.now().minusMinutes(5)); // Expired
        
        when(passwordOtpRepository.findTopByEmailOrderByIdDesc("test@email.com")).thenReturn(Optional.of(otp));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.verifyOtp("test@email.com", "123456"));
        assertEquals("OTP expired", exception.getMessage());
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        // Act
        String result = authService.resetPassword("test@email.com", "newPassword");

        // Assert
        assertEquals("Password reset successfully", result);
        verify(userRepository).save(user);
        verify(passwordOtpRepository).deleteByEmail("test@email.com");
    }
}
