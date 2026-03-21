package com.dala.crm.impl;

import com.dala.crm.dto.KnowledgeBaseArticleCreateRequest;
import com.dala.crm.dto.KnowledgeBaseArticleResponse;
import com.dala.crm.entity.KnowledgeBaseArticle;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.KnowledgeBaseArticleNotFoundException;
import com.dala.crm.repo.KnowledgeBaseArticleRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.KnowledgeBaseArticleService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default knowledge base service implementation.
 */
@Service
@Transactional
public class KnowledgeBaseArticleServiceImpl implements KnowledgeBaseArticleService {

    private final KnowledgeBaseArticleRepository knowledgeBaseArticleRepository;
    private final AuditLogService auditLogService;

    public KnowledgeBaseArticleServiceImpl(
            KnowledgeBaseArticleRepository knowledgeBaseArticleRepository,
            AuditLogService auditLogService
    ) {
        this.knowledgeBaseArticleRepository = knowledgeBaseArticleRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public KnowledgeBaseArticleResponse create(KnowledgeBaseArticleCreateRequest request) {
        KnowledgeBaseArticle article = new KnowledgeBaseArticle();
        article.setTenantId(currentTenant());
        article.setTitle(request.title().trim());
        article.setCategory(request.category().trim());
        article.setBody(request.body().trim());
        article.setPublished(request.published());
        article.setCreatedAt(Instant.now());
        KnowledgeBaseArticle savedArticle = knowledgeBaseArticleRepository.save(article);
        auditLogService.record("CREATE", "KNOWLEDGE_BASE_ARTICLE", savedArticle.getId(), "Created knowledge base article " + savedArticle.getTitle());
        return toResponse(savedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeBaseArticleResponse> list() {
        return knowledgeBaseArticleRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public KnowledgeBaseArticleResponse get(Long id) {
        return toResponse(currentArticle(id));
    }

    private KnowledgeBaseArticle currentArticle(Long id) {
        String tenantId = currentTenant();
        return knowledgeBaseArticleRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new KnowledgeBaseArticleNotFoundException(id));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private KnowledgeBaseArticleResponse toResponse(KnowledgeBaseArticle article) {
        return new KnowledgeBaseArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getCategory(),
                article.getBody(),
                article.isPublished(),
                article.getCreatedAt()
        );
    }
}
