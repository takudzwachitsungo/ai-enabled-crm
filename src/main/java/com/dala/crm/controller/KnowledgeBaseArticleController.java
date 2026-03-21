package com.dala.crm.controller;

import com.dala.crm.dto.KnowledgeBaseArticleCreateRequest;
import com.dala.crm.dto.KnowledgeBaseArticleResponse;
import com.dala.crm.service.KnowledgeBaseArticleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for knowledge base article management.
 */
@RestController
@RequestMapping("/api/v1/knowledge-base")
public class KnowledgeBaseArticleController {

    private final KnowledgeBaseArticleService knowledgeBaseArticleService;

    public KnowledgeBaseArticleController(KnowledgeBaseArticleService knowledgeBaseArticleService) {
        this.knowledgeBaseArticleService = knowledgeBaseArticleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).KNOWLEDGE_BASE_WRITE)")
    public KnowledgeBaseArticleResponse create(@Valid @RequestBody KnowledgeBaseArticleCreateRequest request) {
        return knowledgeBaseArticleService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).KNOWLEDGE_BASE_READ)")
    public List<KnowledgeBaseArticleResponse> list() {
        return knowledgeBaseArticleService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).KNOWLEDGE_BASE_READ)")
    public KnowledgeBaseArticleResponse get(@PathVariable Long id) {
        return knowledgeBaseArticleService.get(id);
    }
}
