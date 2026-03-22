package com.dala.crm.service;

import com.dala.crm.dto.AuthLoginRequest;
import com.dala.crm.dto.AuthSessionResponse;
import com.dala.crm.dto.AuthSignupRequest;
import com.dala.crm.dto.PasswordChangeRequest;
import com.dala.crm.dto.SimpleMessageResponse;
import org.springframework.security.core.Authentication;

/**
 * Authentication and tenant onboarding service contract.
 */
public interface AuthService {

    AuthSessionResponse register(AuthSignupRequest request);

    AuthSessionResponse login(AuthLoginRequest request);

    SimpleMessageResponse changePassword(Authentication authentication, PasswordChangeRequest request);
}
