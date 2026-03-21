package com.dala.crm.repo;

import com.dala.crm.entity.Invoice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA access for invoices.
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByTenantIdOrderByCreatedAtDesc(String tenantId);
}
