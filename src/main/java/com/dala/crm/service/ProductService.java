package com.dala.crm.service;

import com.dala.crm.dto.ProductCreateRequest;
import com.dala.crm.dto.ProductResponse;
import java.util.List;

/**
 * Service contract for product catalog management.
 */
public interface ProductService {

    ProductResponse create(ProductCreateRequest request);

    List<ProductResponse> list();

    ProductResponse get(Long id);
}
