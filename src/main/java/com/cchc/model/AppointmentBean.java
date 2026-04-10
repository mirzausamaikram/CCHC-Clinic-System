package com.cchc.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;

public class AppointmentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int appointmentId;
    private int userId;
    private int clinicId;
    private int serviceId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String status;
    private String notes;
    private Timestamp createdAt;

    public AppointmentBean() {
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getPatientId() {
        return userId;
    }

    public void setPatientId(int patientId) {
        this.userId = patientId;
    }

    public int getClinicServiceId() {
        return serviceId;
    }

    public void setClinicServiceId(int clinicServiceId) {
        this.serviceId = clinicServiceId;
    }

    public Integer getAssignedStaffId() {
        return null;
    }

    public void setAssignedStaffId(Integer assignedStaffId) {
    }

    public void setAppointmentDate(Date appointmentDate) {
        if (appointmentDate != null) {
            this.appointmentDate = appointmentDate.toLocalDate();
        }
    }

    public Time getStartTime() {
        try {
            if (timeSlot != null && timeSlot.contains("-")) {
                String x = timeSlot.split("-")[0];
                if (x.length() == 5) {
                    x = x + ":00";
                }
                return Time.valueOf(x);
            }
            if (timeSlot != null && timeSlot.length() == 5) {
                return Time.valueOf(timeSlot + ":00");
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void setStartTime(Time startTime) {
        if (startTime != null) {
            String s = startTime.toString();
            if (s.length() >= 5) {
                s = s.substring(0, 5);
            }
            this.timeSlot = s;
        }
    }

    public Time getEndTime() {
        try {
            if (timeSlot != null && timeSlot.contains("-")) {
                String x = timeSlot.split("-")[1];
                if (x.length() == 5) {
                    x = x + ":00";
                }
                return Time.valueOf(x);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void setEndTime(Time endTime) {
        if (endTime != null) {
            String s1 = "";
            if (this.timeSlot != null && this.timeSlot.length() >= 5) {
                s1 = this.timeSlot.substring(0, 5);
            }
            String s2 = endTime.toString();
            if (s2.length() >= 5) {
                s2 = s2.substring(0, 5);
            }
            if (!s1.isEmpty()) {
                this.timeSlot = s1 + "-" + s2;
            } else {
                this.timeSlot = s2;
            }
        }
    }

    public String getBookingChannel() {
        return "ONLINE";
    }

    public void setBookingChannel(String bookingChannel) {
    }

    public int getCreatedByUserId() {
        return userId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.userId = createdByUserId;
    }

    public Timestamp getUpdatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
    }
}
