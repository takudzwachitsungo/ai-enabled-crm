package com.dala.crm.controller;

import com.dala.crm.dto.ConversationRecordCreateRequest;
import com.dala.crm.dto.ConversationRecordDto;
import com.dala.crm.service.ConversationRecordService;
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
 * REST endpoints for communication records.
 */
@RestController
@RequestMapping("/api/v1/communications")
public class ConversationRecordController {

    private final ConversationRecordService conversationRecordService;

    public ConversationRecordController(ConversationRecordService conversationRecordService) {
        this.conversationRecordService = conversationRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).COMMUNICATIONS_WRITE)")
    public ConversationRecordDto create(@Valid @RequestBody ConversationRecordCreateRequest request) {
        return conversationRecordService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).COMMUNICATIONS_READ)")
    public List<ConversationRecordDto> list() {
        return conversationRecordService.list();
    }
}
