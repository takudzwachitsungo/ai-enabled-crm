package com.dala.crm.controller;

import com.dala.crm.dto.AiDraftRequest;
import com.dala.crm.dto.AiInteractionDto;
import com.dala.crm.dto.AiSummarizeRequest;
import com.dala.crm.service.AiInteractionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for AI summaries, drafts, and trace history.
 */
@RestController
@RequestMapping("/api/v1/ai")
public class AiInteractionController {

    private final AiInteractionService aiInteractionService;

    public AiInteractionController(AiInteractionService aiInteractionService) {
        this.aiInteractionService = aiInteractionService;
    }

    @PostMapping("/summarize")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).AI_INTERACTIONS_WRITE)")
    public AiInteractionDto summarize(@Valid @RequestBody AiSummarizeRequest request) {
        return aiInteractionService.summarize(request);
    }

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).AI_INTERACTIONS_WRITE)")
    public AiInteractionDto draft(@Valid @RequestBody AiDraftRequest request) {
        return aiInteractionService.draft(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).AI_INTERACTIONS_READ)")
    public List<AiInteractionDto> list() {
        return aiInteractionService.list();
    }
}
