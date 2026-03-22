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
        String tenantId = resolveCurrentTenantId();
        if (tenantId == null || tenantId.isBlank() || appUserRepository == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return loadTenantPrincipal(tenantId, username);
    }

    public TenantUserPrincipal loadTenantPrincipal(String tenantId, String username) throws UsernameNotFoundException {
        TenantUserPrincipal demoPrincipal = demoUser(tenantId, username);
        if (demoPrincipal != null) {
            return demoPrincipal;
        }

        AppUser appUser = appUserRepository.findByTenantIdAndEmailIgnoreCase(tenantId, username)
                .filter(AppUser::isActive)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new TenantUserPrincipal(
                appUser.getId(),
                tenantId,
                resolveTenantName(tenantId),
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

    private TenantUserPrincipal demoUser(String tenantId, String username) {
        if (!"tenant-demo".equalsIgnoreCase(tenantId)) {
            return null;
        }

        if ("local-dev".equalsIgnoreCase(username)) {
            return new TenantUserPrincipal(
                    0L,
                    tenantId,
                    resolveTenantName(tenantId),
                    "Local Developer",
                    "local-dev",
                    "{noop}local-dev-pass",
                    adminAuthorities()
            );
        }
        if ("local-view".equalsIgnoreCase(username)) {
            return new TenantUserPrincipal(
                    1L,
                    tenantId,
                    resolveTenantName(tenantId),
                    "Local Viewer",
                    "local-view",
                    "{noop}local-view-pass",
                    viewerAuthorities()
            );
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
