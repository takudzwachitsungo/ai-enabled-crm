package com.dala.crm.exception;

/**
 * Thrown when a knowledge base article cannot be found in current tenant scope.
 */
public class KnowledgeBaseArticleNotFoundException extends NotFoundException {

    public KnowledgeBaseArticleNotFoundException(Long id) {
        super("Knowledge base article not found: " + id);
    }
}
