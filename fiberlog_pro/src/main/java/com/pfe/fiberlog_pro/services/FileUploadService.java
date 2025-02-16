package com.pfe.fiberlog_pro.services;

import org.springframework.web.multipart.MultipartFile;

import com.pfe.fiberlog_pro.payloads.FileUploadResponseDto;

public interface FileUploadService {
    FileUploadResponseDto processFile(MultipartFile file);
}
