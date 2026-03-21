package com.dala.crm.controller;

import com.dala.crm.dto.AccountCreateRequest;
import com.dala.crm.dto.AccountResponse;
import com.dala.crm.service.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for account management in the CRM core module.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).ACCOUNTS_WRITE)")
    public AccountResponse createAccount(@Valid @RequestBody AccountCreateRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).ACCOUNTS_READ)")
    public List<AccountResponse> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).ACCOUNTS_READ)")
    public AccountResponse getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }
}
