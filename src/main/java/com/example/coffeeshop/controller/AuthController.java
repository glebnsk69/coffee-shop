package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.LoginRequest;
import com.example.coffeeshop.dto.LoginResponse;
import com.example.coffeeshop.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        String token = authService.login(request);

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                3600
        );

        return ResponseEntity.ok(response);
    }
}