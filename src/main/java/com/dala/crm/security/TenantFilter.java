package com.dala.crm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Resolves tenant context from the incoming request header.
 *
 * Header: X-Tenant-Id
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String RESOLVED_TENANT_REQUEST_ATTRIBUTE = TenantFilter.class.getName() + ".resolvedTenantId";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return HttpMethod.OPTIONS.matches(request.getMethod())
                || path.startsWith("/api/health")
                || path.startsWith("/actuator/health");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String tenantId = request.getHeader(TENANT_HEADER);
        String resolvedTenantId = (String) request.getAttribute(RESOLVED_TENANT_REQUEST_ATTRIBUTE);
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (tenantId != null && !tenantId.isBlank() && resolvedTenantId != null && !resolvedTenantId.isBlank()
                && !tenantId.trim().equalsIgnoreCase(resolvedTenantId.trim())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"status":401,"error":"Unauthorized","message":"Tenant header does not match the authenticated session.","path":"%s"}
                    """.formatted(request.getRequestURI()).replace(System.lineSeparator(), "").trim());
            return;
        }

        String effectiveTenantId = tenantId;
        if ((effectiveTenantId == null || effectiveTenantId.isBlank()) && resolvedTenantId != null && !resolvedTenantId.isBlank()) {
            effectiveTenantId = resolvedTenantId;
        }

        if ((authorization != null && !authorization.isBlank()) && (effectiveTenantId == null || effectiveTenantId.isBlank())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"status":400,"error":"Bad Request","message":"Missing required header: X-Tenant-Id","path":"%s"}
                    """.formatted(request.getRequestURI()).replace(System.lineSeparator(), "").trim());
            return;
        }

        if (effectiveTenantId != null && !effectiveTenantId.isBlank()) {
            TenantContext.setTenantId(effectiveTenantId.trim());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
