package com.dala.crm.controller;

import com.dala.crm.dto.QuoteCreateRequest;
import com.dala.crm.dto.QuoteResponse;
import com.dala.crm.service.QuoteService;
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
 * REST endpoints for quote management.
 */
@RestController
@RequestMapping("/api/v1/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).QUOTES_WRITE)")
    public QuoteResponse create(@Valid @RequestBody QuoteCreateRequest request) {
        return quoteService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).QUOTES_READ)")
    public List<QuoteResponse> list() {
        return quoteService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).QUOTES_READ)")
    public QuoteResponse get(@PathVariable Long id) {
        return quoteService.get(id);
    }
}
