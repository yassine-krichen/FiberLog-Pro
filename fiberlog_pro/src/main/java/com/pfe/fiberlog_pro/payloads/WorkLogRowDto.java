package com.pfe.fiberlog_pro.payloads;

import lombok.Data;

@Data
public class WorkLogRowDto {
    private String date;           // e.g. "2025-02-10"
    private String employeeName;
    private String projectName;
    private String taskDescription;
    private Double hoursLogged;
    
    // You could add additional fields for advanced validation
    // Or keep it simple and just parse them here.
}
