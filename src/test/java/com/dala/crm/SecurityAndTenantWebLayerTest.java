package com.dala.crm;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dala.crm.controller.AccountController;
import com.dala.crm.controller.ActivityController;
import com.dala.crm.controller.AiInteractionController;
import com.dala.crm.controller.AudienceSegmentController;
import com.dala.crm.controller.AuditLogController;
import com.dala.crm.controller.CannedResponseController;
import com.dala.crm.controller.CampaignController;
import com.dala.crm.controller.ContactController;
import com.dala.crm.controller.ConversationRecordController;
import com.dala.crm.controller.DashboardController;
import com.dala.crm.controller.HealthController;
import com.dala.crm.controller.IdentityController;
import com.dala.crm.controller.IntegrationConnectionController;
import com.dala.crm.controller.KnowledgeBaseArticleController;
import com.dala.crm.controller.LeadController;
import com.dala.crm.controller.OpportunityController;
import com.dala.crm.controller.ReportSnapshotController;
import com.dala.crm.controller.SlaPolicyController;
import com.dala.crm.controller.TicketController;
import com.dala.crm.controller.TimelineController;
import com.dala.crm.controller.WorkflowDefinitionController;
import com.dala.crm.dto.AccountResponse;
import com.dala.crm.dto.ActivityResponse;
import com.dala.crm.dto.AiInteractionDto;
import com.dala.crm.dto.AudienceSegmentResponse;
import com.dala.crm.dto.AuditLogResponse;
import com.dala.crm.dto.CampaignDeliveryRunResponse;
import com.dala.crm.dto.CampaignResponse;
import com.dala.crm.dto.ContactResponse;
import com.dala.crm.dto.ConversationRecordDto;
import com.dala.crm.dto.DashboardSummaryResponse;
import com.dala.crm.dto.IntegrationConnectionDto;
import com.dala.crm.dto.KnowledgeBaseArticleResponse;
import com.dala.crm.dto.LeadResponse;
import com.dala.crm.dto.OpportunityResponse;
import com.dala.crm.dto.ReportSnapshotDto;
import com.dala.crm.dto.SlaPolicyResponse;
import com.dala.crm.dto.TicketEscalationRunResponse;
import com.dala.crm.dto.TicketResponse;
import com.dala.crm.dto.WorkflowDefinitionDto;
import com.dala.crm.dto.CannedResponseResponse;
import com.dala.crm.exception.GlobalExceptionHandler;
import com.dala.crm.security.SecurityConfig;
import com.dala.crm.security.TenantFilter;
import com.dala.crm.service.AccountService;
import com.dala.crm.service.ActivityService;
import com.dala.crm.service.AiInteractionService;
import com.dala.crm.service.AudienceSegmentService;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.CampaignService;
import com.dala.crm.service.ContactService;
import com.dala.crm.service.ConversationRecordService;
import com.dala.crm.service.DashboardService;
import com.dala.crm.service.IntegrationConnectionService;
import com.dala.crm.service.KnowledgeBaseArticleService;
import com.dala.crm.service.LeadService;
import com.dala.crm.service.OpportunityService;
import com.dala.crm.service.ReportSnapshotService;
import com.dala.crm.service.SlaPolicyService;
import com.dala.crm.service.TicketService;
import com.dala.crm.service.WorkflowDefinitionService;
import com.dala.crm.service.CannedResponseService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        HealthController.class,
        IdentityController.class,
        LeadController.class,
        ContactController.class,
        AccountController.class,
        OpportunityController.class,
        ActivityController.class,
        AudienceSegmentController.class,
        AuditLogController.class,
        TimelineController.class,
        WorkflowDefinitionController.class,
        DashboardController.class,
        IntegrationConnectionController.class,
        KnowledgeBaseArticleController.class,
        CannedResponseController.class,
        CampaignController.class,
        ConversationRecordController.class,
        AiInteractionController.class,
        ReportSnapshotController.class,
        TicketController.class,
        SlaPolicyController.class
})
@Import({SecurityConfig.class, TenantFilter.class, GlobalExceptionHandler.class})
class SecurityAndTenantWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService leadService;

    @MockBean
    private ContactService contactService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private OpportunityService opportunityService;

    @MockBean
    private ActivityService activityService;

    @MockBean
    private AudienceSegmentService audienceSegmentService;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private WorkflowDefinitionService workflowDefinitionService;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private IntegrationConnectionService integrationConnectionService;

    @MockBean
    private KnowledgeBaseArticleService knowledgeBaseArticleService;

    @MockBean
    private CannedResponseService cannedResponseService;

    @MockBean
    private CampaignService campaignService;

    @MockBean
    private ConversationRecordService conversationRecordService;

    @MockBean
    private AiInteractionService aiInteractionService;

    @MockBean
    private ReportSnapshotService reportSnapshotService;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private SlaPolicyService slaPolicyService;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedRequestWithoutTenantHeaderIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/leads")
                        .with(httpBasic("local-dev", "local-dev-pass")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required header: X-Tenant-Id"));
    }

    @Test
    void authenticatedRequestWithTenantHeaderSucceeds() throws Exception {
        when(leadService.getLeads()).thenReturn(List.of(
                new LeadResponse(1L, "Jane Doe", "jane@example.com", "NEW", Instant.parse("2026-03-20T08:00:00Z"))
        ));

        mockMvc.perform(get("/api/v1/leads")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jane@example.com"));
    }

    @Test
    void identityEndpointReturnsCurrentUserAuthorities() throws Exception {
        mockMvc.perform(get("/api/v1/identity/me")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("local-view"))
                .andExpect(jsonPath("$.authorities", hasItems(
                        "crm:identity:read",
                        "crm:leads:read",
                        "crm:contacts:read",
                        "crm:accounts:read",
                        "crm:opportunities:read",
                        "crm:activities:read",
                        "crm:tickets:read",
                        "crm:sla-policies:read",
                        "crm:knowledge-base:read",
                        "crm:canned-responses:read",
                        "crm:audience-segments:read",
                        "crm:campaigns:read",
                        "crm:reports:read",
                        "crm:dashboard:read"
                )));
    }

    @Test
    void viewerCannotCreateLead() throws Exception {
        mockMvc.perform(post("/api/v1/leads")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Jane Doe","email":"jane@example.com"}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadContacts() throws Exception {
        when(contactService.getContacts()).thenReturn(List.of(
                new ContactResponse(11L, "Jane Contact", "contact@example.com", "Acme", Instant.parse("2026-03-20T08:10:00Z"))
        ));

        mockMvc.perform(get("/api/v1/contacts")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Acme"));
    }

    @Test
    void viewerCanReadAccounts() throws Exception {
        when(accountService.getAccounts()).thenReturn(List.of(
                new AccountResponse(21L, "Acme Corp", "Manufacturing", "https://acme.example.com", Instant.parse("2026-03-20T08:15:00Z"))
        ));

        mockMvc.perform(get("/api/v1/accounts")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Acme Corp"));
    }

    @Test
    void viewerCannotCreateAccount() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Acme Corp","industry":"Manufacturing","website":"https://acme.example.com"}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadOpportunities() throws Exception {
        when(opportunityService.getOpportunities()).thenReturn(List.of(
                new OpportunityResponse(
                        31L,
                        "Acme Renewal",
                        "Acme Corp",
                        new BigDecimal("12500.00"),
                        "PROPOSAL",
                        Instant.parse("2026-03-20T08:20:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/opportunities")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stage").value("PROPOSAL"))
                .andExpect(jsonPath("$[0].amount").value(12500.00));
    }

    @Test
    void viewerCannotCreateOpportunity() throws Exception {
        mockMvc.perform(post("/api/v1/opportunities")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Acme Renewal","accountName":"Acme Corp","amount":12500.00,"stage":"PROPOSAL"}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadTimeline() throws Exception {
        when(activityService.getActivities()).thenReturn(List.of(
                new ActivityResponse(
                        41L,
                        "TASK",
                        "Follow up on quote",
                        "OPPORTUNITY",
                        31L,
                        "Call customer on Friday.",
                        Instant.parse("2026-03-20T08:25:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/timeline")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Follow up on quote"));
    }

    @Test
    void viewerCannotCreateActivity() throws Exception {
        mockMvc.perform(post("/api/v1/activities")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"TASK","subject":"Follow up","relatedEntityType":"LEAD","relatedEntityId":1}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadAuditLogs() throws Exception {
        when(auditLogService.getAuditLogs()).thenReturn(List.of(
                new AuditLogResponse(
                        51L,
                        "local-dev",
                        "CREATE",
                        "LEAD",
                        1L,
                        "Created lead Jane Doe",
                        Instant.parse("2026-03-20T08:30:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/audit-logs")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityType").value("LEAD"));
    }

    @Test
    void viewerCannotReadAuditLogs() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadWorkflows() throws Exception {
        when(workflowDefinitionService.list()).thenReturn(List.of(
                new WorkflowDefinitionDto(
                        61L,
                        "Lead follow-up",
                        "LEAD_CREATED",
                        null,
                        "CREATE_ACTIVITY",
                        "Call new lead",
                        "Reach out within one business day.",
                        true,
                        Instant.parse("2026-03-20T08:35:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/workflows")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].triggerType").value("LEAD_CREATED"));
    }

    @Test
    void viewerCanReadDashboardSummary() throws Exception {
        when(dashboardService.getSummary()).thenReturn(new DashboardSummaryResponse(4, 3, 2, 1, 5, 7, 2, 1, 2, 6, 2));

        mockMvc.perform(get("/api/v1/dashboard/summary")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadCount").value(4))
                .andExpect(jsonPath("$.ticketCount").value(7))
                .andExpect(jsonPath("$.communicationCount").value(6));
    }

    @Test
    void viewerCanReadDashboardAnalytics() throws Exception {
        when(dashboardService.getAnalytics()).thenReturn(new com.dala.crm.dto.DashboardAnalyticsResponse(3, 5, 2, 4, 1, 6, 2, 3));

        mockMvc.perform(get("/api/v1/dashboard/analytics")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publishedKnowledgeArticleCount").value(3))
                .andExpect(jsonPath("$.reportSnapshotCount").value(3));
    }

    @Test
    void viewerCanReadTickets() throws Exception {
        when(ticketService.getTickets()).thenReturn(List.of(
                new TicketResponse(
                        101L,
                        "Payment issue",
                        "Customer could not complete payment.",
                        "HIGH",
                        "OPEN",
                        "Support Team",
                        "EMAIL",
                        "ACCOUNT",
                        21L,
                        Instant.parse("2026-03-20T12:00:00Z"),
                        null,
                        Instant.parse("2026-03-20T09:00:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/tickets")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("HIGH"));
    }

    @Test
    void viewerCannotCreateTicket() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Payment issue","description":"Customer could not complete payment.","priority":"HIGH","sourceChannel":"EMAIL"}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateTicketStatus() throws Exception {
        when(ticketService.updateStatus(org.mockito.ArgumentMatchers.eq(101L), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new TicketResponse(
                        101L,
                        "Payment issue",
                        "Customer could not complete payment.",
                        "HIGH",
                        "IN_PROGRESS",
                        "Support Team",
                        "EMAIL",
                        "ACCOUNT",
                        21L,
                        Instant.parse("2026-03-20T12:00:00Z"),
                        null,
                        Instant.parse("2026-03-20T09:00:00Z")
                ));

        mockMvc.perform(patch("/api/v1/tickets/101/status")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_PROGRESS","note":"Support team accepted the ticket."}
                                """.trim()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void viewerCannotUpdateTicketStatus() throws Exception {
        mockMvc.perform(patch("/api/v1/tickets/101/status")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_PROGRESS","note":"Support team accepted the ticket."}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateTicketAssignment() throws Exception {
        when(ticketService.updateAssignment(org.mockito.ArgumentMatchers.eq(101L), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new TicketResponse(
                        101L,
                        "Payment issue",
                        "Customer could not complete payment.",
                        "HIGH",
                        "OPEN",
                        "Escalation Team",
                        "EMAIL",
                        "ACCOUNT",
                        21L,
                        Instant.parse("2026-03-20T12:00:00Z"),
                        null,
                        Instant.parse("2026-03-20T09:00:00Z")
                ));

        mockMvc.perform(patch("/api/v1/tickets/101/assignment")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"assignee":"Escalation Team","note":"Escalated to specialist queue."}
                                """.trim()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignee").value("Escalation Team"));
    }

    @Test
    void adminCanRunTicketEscalations() throws Exception {
        when(ticketService.runEscalations()).thenReturn(new TicketEscalationRunResponse(2));

        mockMvc.perform(post("/api/v1/tickets/escalations/run")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.escalatedCount").value(2));
    }

    @Test
    void viewerCannotRunTicketEscalations() throws Exception {
        mockMvc.perform(post("/api/v1/tickets/escalations/run")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCannotUpdateTicketAssignment() throws Exception {
        mockMvc.perform(patch("/api/v1/tickets/101/assignment")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"assignee":"Escalation Team","note":"Escalated to specialist queue."}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadSlaPolicies() throws Exception {
        when(slaPolicyService.getPolicies()).thenReturn(List.of(
                new SlaPolicyResponse(
                        111L,
                        "High Priority Response",
                        "HIGH",
                        4,
                        true,
                        Instant.parse("2026-03-20T09:10:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/sla-policies")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].responseHours").value(4));
    }

    @Test
    void viewerCannotCreateSlaPolicy() throws Exception {
        mockMvc.perform(post("/api/v1/sla-policies")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"High Priority Response","priority":"HIGH","responseHours":4,"active":true}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadIntegrations() throws Exception {
        when(integrationConnectionService.list()).thenReturn(List.of(
                new IntegrationConnectionDto(
                        71L,
                        "WhatsApp Cloud",
                        "WHATSAPP",
                        "META",
                        "CONNECTED",
                        Instant.parse("2026-03-20T08:40:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/integrations")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channelType").value("WHATSAPP"));
    }

    @Test
    void viewerCanReadKnowledgeBaseArticles() throws Exception {
        when(knowledgeBaseArticleService.list()).thenReturn(List.of(
                new KnowledgeBaseArticleResponse(
                        121L,
                        "Reset payment settings",
                        "BILLING",
                        "Steps to reset payment settings.",
                        true,
                        Instant.parse("2026-03-20T09:20:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/knowledge-base")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("BILLING"));
    }

    @Test
    void viewerCannotCreateKnowledgeBaseArticle() throws Exception {
        mockMvc.perform(post("/api/v1/knowledge-base")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Reset payment settings","category":"BILLING","body":"Steps to reset payment settings.","published":true}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadCannedResponses() throws Exception {
        when(cannedResponseService.list()).thenReturn(List.of(
                new CannedResponseResponse(
                        131L,
                        "Payment issue acknowledgement",
                        "EMAIL",
                        "BILLING",
                        "We are looking into your payment issue.",
                        Instant.parse("2026-03-20T09:30:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/canned-responses")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channelType").value("EMAIL"));
    }

    @Test
    void viewerCannotCreateCannedResponse() throws Exception {
        mockMvc.perform(post("/api/v1/canned-responses")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Payment issue acknowledgement","channelType":"EMAIL","category":"BILLING","body":"We are looking into your payment issue."}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadAudienceSegments() throws Exception {
        when(audienceSegmentService.list()).thenReturn(List.of(
                new AudienceSegmentResponse(
                        141L,
                        "Dormant customers",
                        "ACCOUNT",
                        "lastContactedBefore=2026-01-01",
                        24,
                        true,
                        Instant.parse("2026-03-20T09:40:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/audience-segments")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sourceType").value("ACCOUNT"));
    }

    @Test
    void viewerCannotCreateAudienceSegment() throws Exception {
        mockMvc.perform(post("/api/v1/audience-segments")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dormant customers","sourceType":"ACCOUNT","criteria":"lastContactedBefore=2026-01-01","estimatedSize":24,"active":true}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadCampaigns() throws Exception {
        when(campaignService.list()).thenReturn(List.of(
                new CampaignResponse(
                        151L,
                        "Retention outreach",
                        "EMAIL",
                        "DRAFT",
                        141L,
                        "Dormant customers",
                        "We miss you",
                        "Come back for a product walkthrough.",
                        Instant.parse("2026-03-25T08:00:00Z"),
                        0,
                        null,
                        Instant.parse("2026-03-20T09:45:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/campaigns")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channelType").value("EMAIL"));
    }

    @Test
    void adminCanRunCampaignDelivery() throws Exception {
        when(campaignService.runDelivery(151L)).thenReturn(
                new CampaignDeliveryRunResponse(
                        151L,
                        "Retention outreach",
                        "SENT",
                        24,
                        Instant.parse("2026-03-21T08:00:00Z")
                )
        );

        mockMvc.perform(post("/api/v1/campaigns/151/deliveries/run")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.deliveredCount").value(24));
    }

    @Test
    void viewerCannotRunCampaignDelivery() throws Exception {
        mockMvc.perform(post("/api/v1/campaigns/151/deliveries/run")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCannotCreateCampaign() throws Exception {
        mockMvc.perform(post("/api/v1/campaigns")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Retention outreach","channelType":"EMAIL","status":"DRAFT","audienceSegmentId":141,"subject":"We miss you","body":"Come back for a product walkthrough.","scheduledAt":"2026-03-25T08:00:00Z"}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadReports() throws Exception {
        when(reportSnapshotService.list()).thenReturn(List.of(
                new ReportSnapshotDto(
                        161L,
                        "Weekly service summary",
                        "SERVICE_OVERVIEW",
                        "EMAIL",
                        "WEEKLY",
                        "GENERATED",
                        "openTickets=6; escalatedTickets=2",
                        Instant.parse("2026-03-20T10:00:00Z"),
                        Instant.parse("2026-03-20T10:00:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/reports")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deliveryChannel").value("EMAIL"));
    }

    @Test
    void viewerCannotCreateReport() throws Exception {
        mockMvc.perform(post("/api/v1/reports")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Weekly service summary","reportType":"SERVICE_OVERVIEW","deliveryChannel":"EMAIL","scheduleCadence":"WEEKLY"}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCannotCreateIntegration() throws Exception {
        mockMvc.perform(post("/api/v1/integrations")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"WhatsApp Cloud","channelType":"WHATSAPP","provider":"META","status":"CONNECTED"}
                                """.trim()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadCommunications() throws Exception {
        when(conversationRecordService.list()).thenReturn(List.of(
                new ConversationRecordDto(
                        81L,
                        "Welcome email",
                        "EMAIL",
                        "OUTBOUND",
                        "jane@example.com",
                        "Welcome",
                        "Thanks for your interest.",
                        "LEAD",
                        1L,
                        Instant.parse("2026-03-20T08:45:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/communications")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channelType").value("EMAIL"));
    }

    @Test
    void adminCanCreateAiDraft() throws Exception {
        when(aiInteractionService.draft(org.mockito.ArgumentMatchers.any())).thenReturn(
                new AiInteractionDto(
                        91L,
                        "Welcome follow-up",
                        "DRAFT",
                        "LEAD",
                        1L,
                        "Write a short follow-up email",
                        "Draft (EMAIL, PROFESSIONAL): Write a short follow-up email",
                        "local-mock",
                        Instant.parse("2026-03-20T08:50:00Z")
                )
        );

        mockMvc.perform(post("/api/v1/ai/draft")
                        .with(httpBasic("local-dev", "local-dev-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Welcome follow-up","sourceType":"LEAD","sourceId":1,"instructions":"Write a short follow-up email","channel":"EMAIL","tone":"PROFESSIONAL"}
                                """.trim()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.operationType").value("DRAFT"))
                .andExpect(jsonPath("$.modelName").value("local-mock"));
    }

    @Test
    void viewerCanReadAiInteractionHistory() throws Exception {
        when(aiInteractionService.list()).thenReturn(List.of(
                new AiInteractionDto(
                        92L,
                        "Lead summary",
                        "SUMMARIZE",
                        "LEAD",
                        1L,
                        "Very long lead notes",
                        "Summary: Very long lead notes",
                        "local-mock",
                        Instant.parse("2026-03-20T08:55:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/ai")
                        .with(httpBasic("local-view", "local-view-pass"))
                        .header(TenantFilter.TENANT_HEADER, "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operationType").value("SUMMARIZE"));
    }
}
