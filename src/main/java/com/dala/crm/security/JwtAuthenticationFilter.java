package com.dala.crm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates bearer tokens for tenant-aware browser sessions.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final TenantAwareUserDetailsService tenantAwareUserDetailsService;

    public JwtAuthenticationFilter(
            JwtTokenService jwtTokenService,
            TenantAwareUserDetailsService tenantAwareUserDetailsService
    ) {
        this.jwtTokenService = jwtTokenService;
        this.tenantAwareUserDetailsService = tenantAwareUserDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorization == null || !authorization.startsWith("Bearer ");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorization.substring("Bearer ".length()).trim();

        try {
            JwtTokenService.JwtClaims claims = jwtTokenService.parseAccessToken(token);
            TenantUserPrincipal principal = tenantAwareUserDetailsService.loadTenantPrincipal(
                    claims.tenantId(),
                    claims.email()
            );

            request.setAttribute(TenantFilter.RESOLVED_TENANT_REQUEST_ATTRIBUTE, principal.getTenantId());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException | UsernameNotFoundException ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(("""
                    {"status":401,"error":"Unauthorized","message":"%s","path":"%s"}
                    """.formatted(ex.getMessage(), request.getRequestURI())).replace(System.lineSeparator(), "").trim());
        }
    }
}
