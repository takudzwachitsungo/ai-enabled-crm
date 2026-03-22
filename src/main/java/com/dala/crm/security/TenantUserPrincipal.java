package com.dala.crm.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Authenticated tenant user principal with display metadata.
 */
public class TenantUserPrincipal extends User {

    private final Long userId;
    private final String tenantId;
    private final String tenantName;
    private final String fullName;
    private final String email;

    public TenantUserPrincipal(
            Long userId,
            String tenantId,
            String tenantName,
            String fullName,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(email, password, authorities);
        this.userId = userId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.fullName = fullName;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}
