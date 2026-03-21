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
    public static final String TICKETS_READ = "crm:tickets:read";
    public static final String TICKETS_WRITE = "crm:tickets:write";
    public static final String SLA_POLICIES_READ = "crm:sla-policies:read";
    public static final String SLA_POLICIES_WRITE = "crm:sla-policies:write";
    public static final String KNOWLEDGE_BASE_READ = "crm:knowledge-base:read";
    public static final String KNOWLEDGE_BASE_WRITE = "crm:knowledge-base:write";
    public static final String CANNED_RESPONSES_READ = "crm:canned-responses:read";
    public static final String CANNED_RESPONSES_WRITE = "crm:canned-responses:write";
    public static final String AUDIENCE_SEGMENTS_READ = "crm:audience-segments:read";
    public static final String AUDIENCE_SEGMENTS_WRITE = "crm:audience-segments:write";
    public static final String CAMPAIGNS_READ = "crm:campaigns:read";
    public static final String CAMPAIGNS_WRITE = "crm:campaigns:write";
    public static final String REPORTS_READ = "crm:reports:read";
    public static final String REPORTS_WRITE = "crm:reports:write";
    public static final String PRODUCTS_READ = "crm:products:read";
    public static final String PRODUCTS_WRITE = "crm:products:write";
    public static final String QUOTES_READ = "crm:quotes:read";
    public static final String QUOTES_WRITE = "crm:quotes:write";
    public static final String INVOICES_READ = "crm:invoices:read";
    public static final String INVOICES_WRITE = "crm:invoices:write";
    public static final String COMMERCE_EVENTS_READ = "crm:commerce-events:read";
    public static final String COMMERCE_EVENTS_WRITE = "crm:commerce-events:write";
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
