package com.dala.crm.impl;

import com.dala.crm.dto.InvoiceCreateRequest;
import com.dala.crm.dto.InvoiceResponse;
import com.dala.crm.entity.Account;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.Invoice;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.InvoiceNotFoundException;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.InvoiceRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.InvoiceService;
import java.time.Instant;
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
    private final AccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            AccountRepository accountRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.invoiceRepository = invoiceRepository;
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
