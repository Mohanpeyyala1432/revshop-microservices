package com.revshop.userservice.repository;

import com.revshop.userservice.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordOtpRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findTopByEmailOrderByIdDesc(String email);

    void deleteByEmail(String email);
}


