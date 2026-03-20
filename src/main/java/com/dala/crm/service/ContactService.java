package com.dala.crm.service;

import com.dala.crm.dto.ContactCreateRequest;
import com.dala.crm.dto.ContactResponse;
import java.util.List;

/**
 * Public use-case contract for contact management.
 */
public interface ContactService {

    /**
     * Creates a new contact for the current tenant.
     */
    ContactResponse createContact(ContactCreateRequest request);

    /**
     * Returns all contacts for the current tenant.
     */
    List<ContactResponse> getContacts();

    /**
     * Returns one contact by ID in current tenant scope.
     */
    ContactResponse getContact(Long id);
}
