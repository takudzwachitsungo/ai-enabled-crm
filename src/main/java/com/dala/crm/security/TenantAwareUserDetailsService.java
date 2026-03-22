package com.dala.crm.security;

import com.dala.crm.entity.AppUser;
import com.dala.crm.entity.TenantProfile;
import com.dala.crm.repo.AppUserRepository;
import com.dala.crm.repo.TenantProfileRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * User details service backed by tenant-scoped app users with local demo fallbacks.
 */
public class TenantAwareUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final TenantProfileRepository tenantProfileRepository;

    public TenantAwareUserDetailsService(
            AppUserRepository appUserRepository,
            TenantProfileRepository tenantProfileRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.tenantProfileRepository = tenantProfileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails demoUser = demoUser(username);
        if (demoUser != null) {
            return demoUser;
        }

        String tenantId = resolveCurrentTenantId();
        if (tenantId == null || tenantId.isBlank() || appUserRepository == null) {
            throw new UsernameNotFoundException("User not found");
        }

        AppUser appUser = appUserRepository.findByTenantIdAndEmailIgnoreCase(tenantId, username)
                .filter(AppUser::isActive)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String tenantName = resolveTenantName(tenantId);
        return new TenantUserPrincipal(
                appUser.getId(),
                tenantId,
                tenantName,
                appUser.getFullName(),
                appUser.getEmail(),
                appUser.getPasswordHash(),
                authoritiesForRole(appUser.getRole())
        );
    }

    public Collection<? extends GrantedAuthority> authoritiesForRole(String role) {
        if ("VIEWER".equalsIgnoreCase(role)) {
            return viewerAuthorities();
        }
        return adminAuthorities();
    }

    private String resolveCurrentTenantId() {
        String tenantId = TenantContext.getTenantId().orElse(null);
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId;
        }

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            String headerTenantId = attributes.getRequest().getHeader(TenantFilter.TENANT_HEADER);
            if (headerTenantId == null || headerTenantId.isBlank()) {
                return null;
            }
            return headerTenantId.trim();
        } catch (IllegalStateException ex) {
            return null;
        }
    }

    private String resolveTenantName(String tenantId) {
        if (tenantProfileRepository == null) {
            return tenantId;
        }
        Optional<TenantProfile> tenantProfile = tenantProfileRepository.findByTenantIdIgnoreCase(tenantId);
        return tenantProfile.map(TenantProfile::getName).orElse(tenantId);
    }

    private UserDetails demoUser(String username) {
        if ("local-dev".equalsIgnoreCase(username)) {
            return User.withUsername("local-dev")
                    .password("{noop}local-dev-pass")
                    .authorities(adminAuthorities())
                    .build();
        }
        if ("local-view".equalsIgnoreCase(username)) {
            return User.withUsername("local-view")
                    .password("{noop}local-view-pass")
                    .authorities(viewerAuthorities())
                    .build();
        }
        return null;
    }

    private List<GrantedAuthority> adminAuthorities() {
        return toAuthorities(List.of(
                CrmAuthorities.LEADS_READ,
                CrmAuthorities.LEADS_WRITE,
                CrmAuthorities.CONTACTS_READ,
                CrmAuthorities.CONTACTS_WRITE,
                CrmAuthorities.ACCOUNTS_READ,
                CrmAuthorities.ACCOUNTS_WRITE,
                CrmAuthorities.OPPORTUNITIES_READ,
                CrmAuthorities.OPPORTUNITIES_WRITE,
                CrmAuthorities.ACTIVITIES_READ,
                CrmAuthorities.ACTIVITIES_WRITE,
                CrmAuthorities.WORKFLOWS_READ,
                CrmAuthorities.WORKFLOWS_WRITE,
                CrmAuthorities.CUSTOM_ENTITIES_READ,
                CrmAuthorities.CUSTOM_ENTITIES_WRITE,
                CrmAuthorities.DASHBOARD_READ,
                CrmAuthorities.TICKETS_READ,
                CrmAuthorities.TICKETS_WRITE,
                CrmAuthorities.SLA_POLICIES_READ,
                CrmAuthorities.SLA_POLICIES_WRITE,
                CrmAuthorities.KNOWLEDGE_BASE_READ,
                CrmAuthorities.KNOWLEDGE_BASE_WRITE,
                CrmAuthorities.CANNED_RESPONSES_READ,
                CrmAuthorities.CANNED_RESPONSES_WRITE,
                CrmAuthorities.AUDIENCE_SEGMENTS_READ,
                CrmAuthorities.AUDIENCE_SEGMENTS_WRITE,
                CrmAuthorities.CAMPAIGNS_READ,
                CrmAuthorities.CAMPAIGNS_WRITE,
                CrmAuthorities.REPORTS_READ,
                CrmAuthorities.REPORTS_WRITE,
                CrmAuthorities.PRODUCTS_READ,
                CrmAuthorities.PRODUCTS_WRITE,
                CrmAuthorities.QUOTES_READ,
                CrmAuthorities.QUOTES_WRITE,
                CrmAuthorities.INVOICES_READ,
                CrmAuthorities.INVOICES_WRITE,
                CrmAuthorities.COMMERCE_EVENTS_READ,
                CrmAuthorities.COMMERCE_EVENTS_WRITE,
                CrmAuthorities.INTEGRATIONS_READ,
                CrmAuthorities.INTEGRATIONS_WRITE,
                CrmAuthorities.COMMUNICATIONS_READ,
                CrmAuthorities.COMMUNICATIONS_WRITE,
                CrmAuthorities.AI_INTERACTIONS_READ,
                CrmAuthorities.AI_INTERACTIONS_WRITE,
                CrmAuthorities.AUDIT_READ,
                CrmAuthorities.IDENTITY_READ,
                CrmAuthorities.USERS_READ,
                CrmAuthorities.USERS_WRITE,
                CrmAuthorities.TENANT_PROFILE_READ,
                CrmAuthorities.TENANT_PROFILE_WRITE
        ));
    }

    private List<GrantedAuthority> viewerAuthorities() {
        return toAuthorities(List.of(
                CrmAuthorities.LEADS_READ,
                CrmAuthorities.CONTACTS_READ,
                CrmAuthorities.ACCOUNTS_READ,
                CrmAuthorities.OPPORTUNITIES_READ,
                CrmAuthorities.ACTIVITIES_READ,
                CrmAuthorities.WORKFLOWS_READ,
                CrmAuthorities.CUSTOM_ENTITIES_READ,
                CrmAuthorities.DASHBOARD_READ,
                CrmAuthorities.TICKETS_READ,
                CrmAuthorities.SLA_POLICIES_READ,
                CrmAuthorities.KNOWLEDGE_BASE_READ,
                CrmAuthorities.CANNED_RESPONSES_READ,
                CrmAuthorities.AUDIENCE_SEGMENTS_READ,
                CrmAuthorities.CAMPAIGNS_READ,
                CrmAuthorities.REPORTS_READ,
                CrmAuthorities.PRODUCTS_READ,
                CrmAuthorities.QUOTES_READ,
                CrmAuthorities.INVOICES_READ,
                CrmAuthorities.COMMERCE_EVENTS_READ,
                CrmAuthorities.INTEGRATIONS_READ,
                CrmAuthorities.COMMUNICATIONS_READ,
                CrmAuthorities.AI_INTERACTIONS_READ,
                CrmAuthorities.IDENTITY_READ,
                CrmAuthorities.USERS_READ,
                CrmAuthorities.TENANT_PROFILE_READ
        ));
    }

    private List<GrantedAuthority> toAuthorities(List<String> values) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String value : values) {
            authorities.add(new SimpleGrantedAuthority(value));
        }
        return authorities;
    }
}
