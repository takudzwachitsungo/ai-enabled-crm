package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a knowledge base article.
 */
public record KnowledgeBaseArticleResponse(
        Long id,
        String title,
        String category,
        String body,
        boolean published,
        Instant createdAt
) {
}
