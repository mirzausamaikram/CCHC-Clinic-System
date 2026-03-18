package com.cchc.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class CsvImportLogBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int importId;
    private String importType;
    private String fileName;
    private int totalRows;
    private int successRows;
    private int failedRows;
    private String status;
    private int importedByUserId;
    private Timestamp importedAt;

    public CsvImportLogBean() {
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSuccessRows() {
        return successRows;
    }

    public void setSuccessRows(int successRows) {
        this.successRows = successRows;
    }

    public int getFailedRows() {
        return failedRows;
    }

    public void setFailedRows(int failedRows) {
        this.failedRows = failedRows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getImportedByUserId() {
        return importedByUserId;
    }

    public void setImportedByUserId(int importedByUserId) {
        this.importedByUserId = importedByUserId;
    }

    public Timestamp getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(Timestamp importedAt) {
        this.importedAt = importedAt;
    }
}
