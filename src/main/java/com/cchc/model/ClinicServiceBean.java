package com.cchc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ClinicServiceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int clinicServiceId;
    private int clinicId;
    private int serviceId;
    private int durationMinutes;
    private BigDecimal fee;
    private boolean active;
    private Timestamp createdAt;

    public ClinicServiceBean() {
    }

    public int getClinicServiceId() {
        return clinicServiceId;
    }

    public void setClinicServiceId(int clinicServiceId) {
        this.clinicServiceId = clinicServiceId;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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
