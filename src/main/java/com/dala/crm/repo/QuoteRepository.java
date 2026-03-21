package com.dala.crm.repo;

import com.dala.crm.entity.Quote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA access for quotes.
 */
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantId(String tenantId);
}
