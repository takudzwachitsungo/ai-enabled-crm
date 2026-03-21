package com.dala.crm.service;

import com.dala.crm.dto.KnowledgeBaseArticleCreateRequest;
import com.dala.crm.dto.KnowledgeBaseArticleResponse;
import java.util.List;

/**
 * Public contract for knowledge base article management.
 */
public interface KnowledgeBaseArticleService {

    /**
     * Creates a knowledge base article for the current tenant.
     */
    KnowledgeBaseArticleResponse create(KnowledgeBaseArticleCreateRequest request);

    /**
     * Returns knowledge base articles for the current tenant.
     */
    List<KnowledgeBaseArticleResponse> list();

    /**
     * Returns one knowledge base article in current tenant scope.
     */
    KnowledgeBaseArticleResponse get(Long id);
}
