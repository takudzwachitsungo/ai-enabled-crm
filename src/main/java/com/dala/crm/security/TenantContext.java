package com.dala.crm.security;

import java.util.Optional;

/**
 * Thread-local holder for the tenant identifier extracted from the request.
 */
public final class TenantContext {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Optional<String> getTenantId() {
        return Optional.ofNullable(TENANT_ID.get());
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}
