package com.cchc.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class QueueEntryBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int queueId;
    private int clinicId;
    private int serviceId;
    private int patientId;
    private Integer appointmentId;
    private Date queueDate;
    private int tokenNo;
    private int priorityLevel;
    private String queueStatus;
    private Timestamp calledTime;
    private Timestamp serviceStartTime;
    private Timestamp serviceEndTime;
    private Timestamp createdAt;

    public QueueEntryBean() {
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
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

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Date getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(Date queueDate) {
        this.queueDate = queueDate;
    }

    public int getTokenNo() {
        return tokenNo;
    }

    public void setTokenNo(int tokenNo) {
        this.tokenNo = tokenNo;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }

    public Timestamp getCalledTime() {
        return calledTime;
    }

    public void setCalledTime(Timestamp calledTime) {
        this.calledTime = calledTime;
    }

    public Timestamp getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(Timestamp serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public Timestamp getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(Timestamp serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
