package com.dala.crm.impl;

import com.dala.crm.dto.CommerceStatusUpdateRequest;
import com.dala.crm.dto.InvoiceCreateRequest;
import com.dala.crm.dto.InvoiceResponse;
import com.dala.crm.dto.RenewalAutomationRunResponse;
import com.dala.crm.entity.Account;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.Invoice;
import com.dala.crm.entity.Quote;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.InvoiceNotFoundException;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.InvoiceRepository;
import com.dala.crm.repo.QuoteRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.InvoiceService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default invoice service implementation.
 */
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final QuoteRepository quoteRepository;
    private final AccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            QuoteRepository quoteRepository,
            AccountRepository accountRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.invoiceRepository = invoiceRepository;
        this.quoteRepository = quoteRepository;
        this.accountRepository = accountRepository;
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public InvoiceResponse create(InvoiceCreateRequest request) {
        String tenantId = currentTenant();
        Account account = currentAccount(tenantId, request.accountId());
        Instant now = Instant.now();

        Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.setAccountId(account.getId());
        invoice.setInvoiceNumber(request.invoiceNumber().trim());
        invoice.setAmount(request.amount());
        invoice.setStatus(normalize(request.status()));
        invoice.setDueAt(request.dueAt());
        invoice.setCreatedAt(now);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        auditLogService.record("CREATE", "INVOICE", savedInvoice.getId(), "Created invoice " + savedInvoice.getInvoiceNumber());
        recordActivity(savedInvoice, account.getName(), now);
        return toResponse(savedInvoice, account);
    }

    @Override
    public RenewalAutomationRunResponse runRenewalAutomation() {
        String tenantId = currentTenant();
        Instant now = Instant.now();
        Instant renewalThreshold = now.minus(30, ChronoUnit.DAYS);
        Instant quoteThreshold = now.plus(7, ChronoUnit.DAYS);

        List<Invoice> renewalCandidates = invoiceRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(invoice -> "PAID".equalsIgnoreCase(invoice.getStatus()))
                .filter(invoice -> invoice.getCreatedAt().isBefore(renewalThreshold))
                .toList();

        List<Quote> expiringQuotes = quoteRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(quote -> quote.getValidUntil() != null)
                .filter(quote -> !quote.getValidUntil().isAfter(quoteThreshold))
                .filter(quote -> !List.of("CONVERTED", "EXPIRED").contains(normalize(quote.getStatus())))
                .toList();

        long generatedActivities = 0;

        for (Invoice invoice : renewalCandidates) {
            String subject = "Renewal follow-up: " + invoice.getInvoiceNumber();
            if (!activityRepository.existsByTenantIdAndTypeAndRelatedEntityTypeAndRelatedEntityIdAndSubject(
                    tenantId, "RENEWAL", "INVOICE", invoice.getId(), subject)) {
                Account account = currentAccount(tenantId, invoice.getAccountId());
                createAutomationActivity(
                        tenantId,
                        "INVOICE",
                        invoice.getId(),
                        subject,
                        "Review renewal or upsell options for " + account.getName() + ". Invoice amount: " + invoice.getAmount(),
                        now
                );
                generatedActivities++;
            }
        }

        for (Quote quote : expiringQuotes) {
            String subject = "Quote expiry follow-up: " + quote.getName();
            if (!activityRepository.existsByTenantIdAndTypeAndRelatedEntityTypeAndRelatedEntityIdAndSubject(
                    tenantId, "RENEWAL", "QUOTE", quote.getId(), subject)) {
                Account account = currentAccount(tenantId, quote.getAccountId());
                createAutomationActivity(
                        tenantId,
                        "QUOTE",
                        quote.getId(),
                        subject,
                        "Quote for " + account.getName() + " expires on " + quote.getValidUntil() + ". Re-engage before expiry.",
                        now
                );
                generatedActivities++;
            }
        }

        auditLogService.record(
                "RUN",
                "RENEWAL_AUTOMATION",
                null,
                "Processed renewal automation with " + renewalCandidates.size() + " renewal candidates and "
                        + expiringQuotes.size() + " expiring quotes"
        );

        return new RenewalAutomationRunResponse(
                renewalCandidates.size(),
                expiringQuotes.size(),
                generatedActivities,
                now
        );
    }

    @Override
    public InvoiceResponse updateStatus(Long id, CommerceStatusUpdateRequest request) {
        Invoice invoice = currentInvoice(id);
        invoice.setStatus(normalize(request.status()));
        Invoice savedInvoice = invoiceRepository.save(invoice);
        Account account = currentAccount(savedInvoice.getTenantId(), savedInvoice.getAccountId());
        auditLogService.record("UPDATE_STATUS", "INVOICE", savedInvoice.getId(), "Updated invoice status to " + savedInvoice.getStatus());
        recordStatusActivity(savedInvoice, "Invoice status changed to " + savedInvoice.getStatus(), trimToNull(request.note()));
        return toResponse(savedInvoice, account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> list() {
        String tenantId = currentTenant();
        return invoiceRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(invoice -> toResponse(invoice, currentAccount(tenantId, invoice.getAccountId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse get(Long id) {
        Invoice invoice = currentInvoice(id);
        Account account = currentAccount(invoice.getTenantId(), invoice.getAccountId());
        return toResponse(invoice, account);
    }

    private void recordActivity(Invoice invoice, String accountName, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(invoice.getTenantId());
        activity.setType("INVOICE");
        activity.setSubject("Invoice created: " + invoice.getInvoiceNumber());
        activity.setRelatedEntityType("INVOICE");
        activity.setRelatedEntityId(invoice.getId());
        activity.setDetails("Account: " + accountName + ", amount: " + invoice.getAmount());
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private void createAutomationActivity(
            String tenantId,
            String relatedEntityType,
            Long relatedEntityId,
            String subject,
            String details,
            Instant createdAt
    ) {
        Activity activity = new Activity();
        activity.setTenantId(tenantId);
        activity.setType("RENEWAL");
        activity.setSubject(subject);
        activity.setRelatedEntityType(relatedEntityType);
        activity.setRelatedEntityId(relatedEntityId);
        activity.setDetails(details);
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private void recordStatusActivity(Invoice invoice, String subject, String details) {
        Activity activity = new Activity();
        activity.setTenantId(invoice.getTenantId());
        activity.setType("INVOICE");
        activity.setSubject(subject);
        activity.setRelatedEntityType("INVOICE");
        activity.setRelatedEntityId(invoice.getId());
        activity.setDetails(details);
        activity.setCreatedAt(Instant.now());
        activityRepository.save(activity);
    }

    private Invoice currentInvoice(Long id) {
        String tenantId = currentTenant();
        return invoiceRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new InvoiceNotFoundException(id));
    }

    private Account currentAccount(String tenantId, Long accountId) {
        return accountRepository.findById(accountId)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadRequestException("Account not found: " + accountId));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private InvoiceResponse toResponse(Invoice invoice, Account account) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getAccountId(),
                account.getName(),
                invoice.getInvoiceNumber(),
                invoice.getAmount(),
                invoice.getStatus(),
                invoice.getDueAt(),
                invoice.getCreatedAt()
        );
    }
}
