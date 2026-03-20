package com.dala.crm.impl;

import com.dala.crm.dto.AccountCreateRequest;
import com.dala.crm.dto.AccountResponse;
import com.dala.crm.entity.Account;
import com.dala.crm.exception.AccountNotFoundException;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AccountService;
import com.dala.crm.service.AuditLogService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default account service implementation for MVP CRUD operations.
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuditLogService auditLogService;

    public AccountServiceImpl(AccountRepository accountRepository, AuditLogService auditLogService) {
        this.accountRepository = accountRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public AccountResponse createAccount(AccountCreateRequest request) {
        Account account = new Account();
        account.setTenantId(currentTenant());
        account.setName(request.name().trim());
        account.setIndustry(trimToNull(request.industry()));
        account.setWebsite(trimToNull(request.website()));
        account.setCreatedAt(Instant.now());
        Account savedAccount = accountRepository.save(account);
        auditLogService.record("CREATE", "ACCOUNT", savedAccount.getId(), "Created account " + savedAccount.getName());
        return toResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccounts() {
        return accountRepository.findByTenantId(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        String tenantId = currentTenant();
        Account account = accountRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new AccountNotFoundException(id));
        return toResponse(account);
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getIndustry(),
                account.getWebsite(),
                account.getCreatedAt()
        );
    }
}
