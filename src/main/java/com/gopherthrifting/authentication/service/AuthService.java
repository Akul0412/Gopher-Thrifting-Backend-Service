package com.gopherthrifting.authentication.service;

import com.gopherthrifting.authentication.model.User;
import com.gopherthrifting.authentication.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       OtpService otpService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.jwtService = jwtService;
    }

    public void register(String name, String email, String password) {
        if (!email.endsWith("@umn.edu")) {
            throw new IllegalArgumentException("Only @umn.edu email addresses are allowed");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        otpService.generateAndSendOtp(email);
    }

    public void verifyOtp(String email, String otpCode) {
        if (!otpService.validateOtp(email, otpCode)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setVerified(true);
        userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!user.isVerified()) {
            throw new IllegalArgumentException("Please verify your email before logging in");
        }

        return jwtService.generateToken(email);
    }

    public void forgotPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("No account found with this email");
        }
        otpService.generateAndSendOtp(email);
    }

    public void resetPassword(String email, String otpCode, String newPassword) {
        if (!otpService.validateOtp(email, otpCode)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
