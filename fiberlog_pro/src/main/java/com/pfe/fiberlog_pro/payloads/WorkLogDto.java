package com.pfe.fiberlog_pro.payloads;

import lombok.Data;

@Data
public class WorkLogDto {
    private Long id;                   // for update or retrieval
    private String date;               // "YYYY-MM-DD" format (as a string for simplicity)
    private String employeeName;
    private String projectName;
    private String taskDescription;
    private Double hoursLogged;
    private boolean validLog;          // indicate if the row is valid or not

    // Reference to metadata
    private Long fileMetadataId;       // ID of FileUploadMetadata
}
