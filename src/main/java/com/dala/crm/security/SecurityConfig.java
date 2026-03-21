package com.dala.crm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security baseline for local development and early Phase 1 RBAC validation.
 *
 * TODO: Replace in-memory users with JWT/OAuth2 resource server integration.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health/**", "/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("local-dev")
                .password("{noop}local-dev-pass")
                .authorities(
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
                        CrmAuthorities.IDENTITY_READ
                )
                .build();

        UserDetails viewer = User.withUsername("local-view")
                .password("{noop}local-view-pass")
                .authorities(
                        CrmAuthorities.LEADS_READ,
                        CrmAuthorities.CONTACTS_READ,
                        CrmAuthorities.ACCOUNTS_READ,
                        CrmAuthorities.OPPORTUNITIES_READ,
                        CrmAuthorities.ACTIVITIES_READ,
                        CrmAuthorities.WORKFLOWS_READ,
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
                        CrmAuthorities.IDENTITY_READ
                )
                .build();

        return new InMemoryUserDetailsManager(admin, viewer);
    }
}
