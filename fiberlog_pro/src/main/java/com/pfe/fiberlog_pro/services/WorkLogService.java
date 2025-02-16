package com.pfe.fiberlog_pro.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pfe.fiberlog_pro.payloads.WorkLogDto;

public interface WorkLogService {
    /**
     * Retrieves a paginated, sortable list of work logs.
     */
    Page<WorkLogDto> getAllWorkLogs(Pageable pageable);

    /**
     * Retrieves a single work log by ID.
     */
    WorkLogDto getWorkLogById(Long id);

    /**
     * Creates a new work log, referencing the given FileUploadMetadata.
     * Also updates the metadata entity (e.g., numberOfRecords, status).
     */
    WorkLogDto createWorkLog(WorkLogDto workLogDto);

    /**
     * Updates an existing work log.
     */
    WorkLogDto updateWorkLog(Long id, WorkLogDto workLogDto);

    /**
     * Deletes a work log by ID, and possibly updates the associated metadata.
     */
    void deleteWorkLog(Long id);
}
