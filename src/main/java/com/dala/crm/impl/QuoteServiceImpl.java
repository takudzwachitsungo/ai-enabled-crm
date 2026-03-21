package com.dala.crm.impl;

import com.dala.crm.dto.InvoiceResponse;
import com.dala.crm.dto.QuoteCreateRequest;
import com.dala.crm.dto.QuoteResponse;
import com.dala.crm.entity.Account;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.Invoice;
import com.dala.crm.entity.Quote;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.QuoteNotFoundException;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.InvoiceRepository;
import com.dala.crm.repo.QuoteRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.QuoteService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default quote service implementation.
 */
@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public QuoteServiceImpl(
            QuoteRepository quoteRepository,
            InvoiceRepository invoiceRepository,
            AccountRepository accountRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.quoteRepository = quoteRepository;
        this.invoiceRepository = invoiceRepository;
        this.accountRepository = accountRepository;
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public QuoteResponse create(QuoteCreateRequest request) {
        String tenantId = currentTenant();
        Account account = currentAccount(tenantId, request.accountId());
        Instant now = Instant.now();

        Quote quote = new Quote();
        quote.setTenantId(tenantId);
        quote.setAccountId(account.getId());
        quote.setName(request.name().trim());
        quote.setAmount(request.amount());
        quote.setStatus(normalize(request.status()));
        quote.setValidUntil(request.validUntil());
        quote.setCreatedAt(now);

        Quote savedQuote = quoteRepository.save(quote);
        auditLogService.record("CREATE", "QUOTE", savedQuote.getId(), "Created quote " + savedQuote.getName());
        recordActivity(savedQuote, account.getName(), now);
        return toResponse(savedQuote, account);
    }

    @Override
    public InvoiceResponse convertToInvoice(Long id) {
        Quote quote = currentQuote(id);
        if ("CONVERTED".equalsIgnoreCase(quote.getStatus())) {
            throw new BadRequestException("Quote already converted to invoice: " + id);
        }

        Account account = currentAccount(quote.getTenantId(), quote.getAccountId());
        Instant now = Instant.now();

        Invoice invoice = new Invoice();
        invoice.setTenantId(quote.getTenantId());
        invoice.setAccountId(quote.getAccountId());
        invoice.setInvoiceNumber("INV-Q" + quote.getId());
        invoice.setAmount(quote.getAmount());
        invoice.setStatus("ISSUED");
        invoice.setDueAt(quote.getValidUntil() != null ? quote.getValidUntil() : now.plusSeconds(14L * 24 * 60 * 60));
        invoice.setCreatedAt(now);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        quote.setStatus("CONVERTED");
        quoteRepository.save(quote);

        auditLogService.record("CONVERT", "QUOTE", quote.getId(), "Converted quote " + quote.getName() + " to invoice " + savedInvoice.getInvoiceNumber());
        auditLogService.record("CREATE", "INVOICE", savedInvoice.getId(), "Created invoice " + savedInvoice.getInvoiceNumber() + " from quote " + quote.getName());
        recordConversionActivity(quote, savedInvoice, account.getName(), now);
        return toInvoiceResponse(savedInvoice, account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteResponse> list() {
        String tenantId = currentTenant();
        return quoteRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(quote -> toResponse(quote, currentAccount(tenantId, quote.getAccountId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteResponse get(Long id) {
        Quote quote = currentQuote(id);
        Account account = currentAccount(quote.getTenantId(), quote.getAccountId());
        return toResponse(quote, account);
    }

    private void recordActivity(Quote quote, String accountName, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(quote.getTenantId());
        activity.setType("QUOTE");
        activity.setSubject("Quote created: " + quote.getName());
        activity.setRelatedEntityType("QUOTE");
        activity.setRelatedEntityId(quote.getId());
        activity.setDetails("Account: " + accountName + ", amount: " + quote.getAmount());
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private void recordConversionActivity(Quote quote, Invoice invoice, String accountName, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(quote.getTenantId());
        activity.setType("INVOICE");
        activity.setSubject("Quote converted to invoice: " + invoice.getInvoiceNumber());
        activity.setRelatedEntityType("QUOTE");
        activity.setRelatedEntityId(quote.getId());
        activity.setDetails("Account: " + accountName + ", quote: " + quote.getName() + ", amount: " + invoice.getAmount());
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private Quote currentQuote(Long id) {
        String tenantId = currentTenant();
        return quoteRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new QuoteNotFoundException(id));
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

    private QuoteResponse toResponse(Quote quote, Account account) {
        return new QuoteResponse(
                quote.getId(),
                quote.getAccountId(),
                account.getName(),
                quote.getName(),
                quote.getAmount(),
                quote.getStatus(),
                quote.getValidUntil(),
                quote.getCreatedAt()
        );
    }

    private InvoiceResponse toInvoiceResponse(Invoice invoice, Account account) {
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
