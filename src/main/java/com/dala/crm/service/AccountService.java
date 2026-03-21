package com.dala.crm.service;

import com.dala.crm.dto.AccountCreateRequest;
import com.dala.crm.dto.AccountResponse;
import java.util.List;

/**
 * Public use-case contract for account management.
 */
public interface AccountService {

    /**
     * Creates a new account for the current tenant.
     */
    AccountResponse createAccount(AccountCreateRequest request);

    /**
     * Returns all accounts for the current tenant.
     */
    List<AccountResponse> getAccounts();

    /**
     * Returns one account by ID in current tenant scope.
     */
    AccountResponse getAccount(Long id);
}
