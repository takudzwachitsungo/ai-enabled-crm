package com.dala.crm.exception;

/**
 * Thrown when a product cannot be found in current tenant scope.
 */
public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }
}
