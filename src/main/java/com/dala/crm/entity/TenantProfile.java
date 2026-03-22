package com.dala.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * MVP placeholder entity for the identitytenancy module.
 */
@Entity
@Table(name = "identitytenancy_tenantprofile")
public class TenantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String tenantId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 40)
    private String deploymentModel;

    @Column(length = 40)
    private String deploymentStatus;

    @Column(length = 80)
    private String deploymentRegion;

    @Column(length = 160)
    private String dedicatedInstanceKey;

    @Column
    private Instant updatedAt;

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

    public String getDeploymentModel() {
        return deploymentModel;
    }

    public void setDeploymentModel(String deploymentModel) {
        this.deploymentModel = deploymentModel;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public String getDeploymentRegion() {
        return deploymentRegion;
    }

    public void setDeploymentRegion(String deploymentRegion) {
        this.deploymentRegion = deploymentRegion;
    }

    public String getDedicatedInstanceKey() {
        return dedicatedInstanceKey;
    }

    public void setDedicatedInstanceKey(String dedicatedInstanceKey) {
        this.dedicatedInstanceKey = dedicatedInstanceKey;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
