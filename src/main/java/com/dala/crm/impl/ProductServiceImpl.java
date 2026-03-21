package com.dala.crm.impl;

import com.dala.crm.dto.ProductCreateRequest;
import com.dala.crm.dto.ProductResponse;
import com.dala.crm.entity.Product;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.ProductNotFoundException;
import com.dala.crm.repo.ProductRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.ProductService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default product service implementation.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;

    public ProductServiceImpl(ProductRepository productRepository, AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public ProductResponse create(ProductCreateRequest request) {
        Product product = new Product();
        product.setTenantId(currentTenant());
        product.setName(request.name().trim());
        product.setDescription(trimToNull(request.description()));
        product.setUnitPrice(request.unitPrice());
        product.setStatus(normalize(request.status()));
        product.setCreatedAt(Instant.now());
        Product savedProduct = productRepository.save(product);
        auditLogService.record("CREATE", "PRODUCT", savedProduct.getId(), "Created product " + savedProduct.getName());
        return toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return productRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return toResponse(currentProduct(id));
    }

    private Product currentProduct(Long id) {
        String tenantId = currentTenant();
        return productRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getUnitPrice(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}
