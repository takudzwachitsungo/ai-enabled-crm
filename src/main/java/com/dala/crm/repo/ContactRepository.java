package com.dala.crm.repo;

import com.dala.crm.entity.Contact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for contact persistence.
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByTenantId(String tenantId);

    long countByTenantId(String tenantId);
}
