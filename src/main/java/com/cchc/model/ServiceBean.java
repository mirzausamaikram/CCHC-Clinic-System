package com.cchc.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ServiceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int serviceId;
    private String serviceCode;
    private String serviceName;
    private String serviceDescription;
    private int defaultDurationMinutes;
    private int quotaPerSlot = 9999;
    private boolean active;
    private Timestamp createdAt;

    public ServiceBean() {
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }

    public void setDefaultDurationMinutes(int defaultDurationMinutes) {
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public int getQuotaPerSlot() {
        return quotaPerSlot;
    }

    public void setQuotaPerSlot(int quotaPerSlot) {
        this.quotaPerSlot = quotaPerSlot;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
