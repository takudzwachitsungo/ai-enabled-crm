package com.dala.crm.controller;

import com.dala.crm.dto.ContactCreateRequest;
import com.dala.crm.dto.ContactResponse;
import com.dala.crm.service.ContactService;
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
 * REST endpoints for contact management in the CRM core module.
 */
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CONTACTS_WRITE)")
    public ContactResponse createContact(@Valid @RequestBody ContactCreateRequest request) {
        return contactService.createContact(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CONTACTS_READ)")
    public List<ContactResponse> getContacts() {
        return contactService.getContacts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CONTACTS_READ)")
    public ContactResponse getContact(@PathVariable Long id) {
        return contactService.getContact(id);
    }
}
