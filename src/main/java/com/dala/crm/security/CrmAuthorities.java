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
    public static final String WORKFLOWS_READ = "crm:workflows:read";
    public static final String WORKFLOWS_WRITE = "crm:workflows:write";
    public static final String DASHBOARD_READ = "crm:dashboard:read";
    public static final String INTEGRATIONS_READ = "crm:integrations:read";
    public static final String INTEGRATIONS_WRITE = "crm:integrations:write";
    public static final String COMMUNICATIONS_READ = "crm:communications:read";
    public static final String COMMUNICATIONS_WRITE = "crm:communications:write";
    public static final String AI_INTERACTIONS_READ = "crm:ai:read";
    public static final String AI_INTERACTIONS_WRITE = "crm:ai:write";
    public static final String AUDIT_READ = "crm:audit:read";
    public static final String IDENTITY_READ = "crm:identity:read";

    private CrmAuthorities() {
    }
}
