package com.dala.crm.exception;

/**
 * Raised when an account ID is not present within the current tenant scope.
 */
public class AccountNotFoundException extends NotFoundException {

    public AccountNotFoundException(Long accountId) {
        super("Account not found: " + accountId);
    }
}
