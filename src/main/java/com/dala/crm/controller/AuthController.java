package com.dala.crm.controller;

import com.dala.crm.dto.AuthLoginRequest;
import com.dala.crm.dto.AuthSessionResponse;
import com.dala.crm.dto.AuthSignupRequest;
import com.dala.crm.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public endpoints for tenant signup and login.
 */
@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthSessionResponse signup(@Valid @RequestBody AuthSignupRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthSessionResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }
}
