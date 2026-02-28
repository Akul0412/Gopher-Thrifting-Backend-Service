package com.gopherthrifting.authentication.service;

import com.gopherthrifting.authentication.model.Otp;
import com.gopherthrifting.authentication.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${otp.expiration.minutes}")
    private int otpExpirationMinutes;

    public OtpService(OtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void generateAndSendOtp(String email) {
        otpRepository.deleteByEmail(email);

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(code);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        otpRepository.save(otp);

        emailService.sendOtpEmail(email, code);
    }

    public boolean validateOtp(String email, String code) {
        return otpRepository.findByEmailAndOtpCodeAndUsedFalse(email, code)
                .filter(otp -> otp.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(otp -> {
                    otp.setUsed(true);
                    otpRepository.save(otp);
                    return true;
                })
                .orElse(false);
    }
}
