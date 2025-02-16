package com.pfe.fiberlog_pro.payloads;

import lombok.Data;

import java.util.List;

@Data
public class FileUploadResponseDto {
    private String status;                // e.g. "SUCCESS", "PARTIAL", "FAILED"
    private int totalRecordsProcessed;
    private int invalidRecordsCount;
    private String message;
    private List<String> errors;          // capture row-specific or general errors
}