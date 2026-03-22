package com.dala.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Tenant-scoped integration connection for communication channels.
 */
@Entity
@Table(name = "integration_integrationconnection")
public class IntegrationConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String tenantId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 40)
    private String channelType;

    @Column(nullable = false, length = 80)
    private String provider;

    @Column(length = 80)
    private String marketplaceAppKey;

    @Column(length = 40)
    private String marketplaceVersion;

    @Column(nullable = false, length = 40)
    private String status;

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

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getMarketplaceAppKey() {
        return marketplaceAppKey;
    }

    public void setMarketplaceAppKey(String marketplaceAppKey) {
        this.marketplaceAppKey = marketplaceAppKey;
    }

    public String getMarketplaceVersion() {
        return marketplaceVersion;
    }

    public void setMarketplaceVersion(String marketplaceVersion) {
        this.marketplaceVersion = marketplaceVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
