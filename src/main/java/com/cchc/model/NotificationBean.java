package com.cchc.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class NotificationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private String notificationType;
    private Integer relatedAppointmentId;
    private boolean read;
    private Timestamp createdAt;

    public NotificationBean() {
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Integer getRelatedAppointmentId() {
        return relatedAppointmentId;
    }

    public void setRelatedAppointmentId(Integer relatedAppointmentId) {
        this.relatedAppointmentId = relatedAppointmentId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
