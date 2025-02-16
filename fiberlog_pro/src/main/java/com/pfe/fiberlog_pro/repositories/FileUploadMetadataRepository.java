package com.pfe.fiberlog_pro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfe.fiberlog_pro.entities.FileUploadMetadata;

@Repository
public interface FileUploadMetadataRepository extends JpaRepository<FileUploadMetadata, Long> {
    // Additional queries if needed
}