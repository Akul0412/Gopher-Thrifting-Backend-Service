package com.gopherthrifting.authentication.repository;

import com.gopherthrifting.authentication.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndOtpCodeAndUsedFalse(String email, String otpCode);
    void deleteByEmail(String email);
}
