package com.dala.crm.repo;

import com.dala.crm.entity.KnowledgeBaseArticle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for knowledge base articles.
 */
public interface KnowledgeBaseArticleRepository extends JpaRepository<KnowledgeBaseArticle, Long> {

    List<KnowledgeBaseArticle> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantIdAndPublishedTrue(String tenantId);
}
