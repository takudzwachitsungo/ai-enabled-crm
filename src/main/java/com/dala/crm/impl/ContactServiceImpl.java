package com.dala.crm.impl;

import com.dala.crm.dto.ContactCreateRequest;
import com.dala.crm.dto.ContactResponse;
import com.dala.crm.entity.Contact;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.ContactNotFoundException;
import com.dala.crm.repo.ContactRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.ContactService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default contact service implementation for MVP CRUD operations.
 */
@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public ContactResponse createContact(ContactCreateRequest request) {
        Contact contact = new Contact();
        contact.setTenantId(currentTenant());
        contact.setFullName(request.fullName().trim());
        contact.setEmail(request.email().trim().toLowerCase());
        contact.setCompanyName(trimToNull(request.companyName()));
        contact.setCreatedAt(Instant.now());
        return toResponse(contactRepository.save(contact));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> getContacts() {
        return contactRepository.findByTenantId(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponse getContact(Long id) {
        String tenantId = currentTenant();
        Contact contact = contactRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ContactNotFoundException(id));
        return toResponse(contact);
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

    private ContactResponse toResponse(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getFullName(),
                contact.getEmail(),
                contact.getCompanyName(),
                contact.getCreatedAt()
        );
    }
}
