package com.dala.crm;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dala.crm.controller.HealthController;
import com.dala.crm.controller.IdentityController;
import com.dala.crm.controller.LeadController;
import com.dala.crm.controller.ContactController;
import com.dala.crm.controller.AccountController;
import com.dala.crm.controller.OpportunityController;
import com.dala.crm.dto.AccountResponse;
import com.dala.crm.dto.ContactResponse;
import com.dala.crm.dto.LeadResponse;
import com.dala.crm.dto.OpportunityResponse;
import com.dala.crm.exception.GlobalExceptionHandler;
import com.dala.crm.security.SecurityConfig;
import com.dala.crm.security.TenantFilter;
import com.dala.crm.service.AccountService;
import com.dala.crm.service.ContactService;
import com.dala.crm.service.LeadService;
import com.dala.crm.service.OpportunityService;
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
        OpportunityController.class
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
                        "crm:opportunities:read"
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
}
