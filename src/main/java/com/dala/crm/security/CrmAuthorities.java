package com.dala.crm.security;

/**
 * Shared authority constants used by endpoint and service authorization rules.
 */
public final class CrmAuthorities {

    public static final String LEADS_READ = "crm:leads:read";
    public static final String LEADS_WRITE = "crm:leads:write";
    public static final String CONTACTS_READ = "crm:contacts:read";
    public static final String CONTACTS_WRITE = "crm:contacts:write";
    public static final String ACCOUNTS_READ = "crm:accounts:read";
    public static final String ACCOUNTS_WRITE = "crm:accounts:write";
    public static final String OPPORTUNITIES_READ = "crm:opportunities:read";
    public static final String OPPORTUNITIES_WRITE = "crm:opportunities:write";
    public static final String ACTIVITIES_READ = "crm:activities:read";
    public static final String ACTIVITIES_WRITE = "crm:activities:write";
    public static final String AUDIT_READ = "crm:audit:read";
    public static final String IDENTITY_READ = "crm:identity:read";

    private CrmAuthorities() {
    }
}
