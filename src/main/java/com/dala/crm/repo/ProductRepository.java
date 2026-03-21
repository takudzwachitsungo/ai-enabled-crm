package com.dala.crm.repo;

import com.dala.crm.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA access for products.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTenantIdOrderByCreatedAtDesc(String tenantId);
}
