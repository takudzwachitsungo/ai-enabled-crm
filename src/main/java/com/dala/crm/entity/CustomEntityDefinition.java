package com.dala.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Tenant-scoped metadata describing a custom entity extension.
 */
@Entity
@Table(name = "platform_customentitydefinition")
public class CustomEntityDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String tenantId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 100)
    private String apiName;

    @Column(length = 160)
    private String pluralLabel;

    @Column(nullable = false, length = 8000)
    private String fieldSchemaJson;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getPluralLabel() {
        return pluralLabel;
    }

    public void setPluralLabel(String pluralLabel) {
        this.pluralLabel = pluralLabel;
    }

    public String getFieldSchemaJson() {
        return fieldSchemaJson;
    }

    public void setFieldSchemaJson(String fieldSchemaJson) {
        this.fieldSchemaJson = fieldSchemaJson;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
