package com.gopherthrifting.authentication.controller;

import com.gopherthrifting.authentication.dto.*;
import com.gopherthrifting.authentication.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.getName(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new ApiResponse("Registration successful. Please check your email for the verification code.", true));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtpCode());
        return ResponseEntity.ok(new ApiResponse("Email verified successfully.", true));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token, "Login successful."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Password reset code sent to your email.", true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getOtpCode(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse("Password reset successful.", true));
    }
}
