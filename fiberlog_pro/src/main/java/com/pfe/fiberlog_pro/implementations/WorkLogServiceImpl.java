package com.pfe.fiberlog_pro.implementations;

import org.springframework.stereotype.Service;

import com.pfe.fiberlog_pro.entities.FileUploadMetadata;
import com.pfe.fiberlog_pro.entities.WorkLogEntry;
import com.pfe.fiberlog_pro.payloads.WorkLogDto;
import com.pfe.fiberlog_pro.repositories.FileUploadMetadataRepository;
import com.pfe.fiberlog_pro.repositories.WorkLogEntryRepository;
import com.pfe.fiberlog_pro.services.WorkLogService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogEntryRepository workLogEntryRepository;
    private final FileUploadMetadataRepository metadataRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<WorkLogDto> getAllWorkLogs(Pageable pageable) {
        Page<WorkLogEntry> page = workLogEntryRepository.findAll(pageable);
        return page.map(this::convertToDto);
    }

    @Override
    public WorkLogDto getWorkLogById(Long id) {
        WorkLogEntry entry = workLogEntryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("WorkLogEntry not found with ID " + id));
        return convertToDto(entry);
    }

    @Override
    public WorkLogDto createWorkLog(WorkLogDto workLogDto) {
        // 1. Validate if FileUploadMetadata exists
        FileUploadMetadata metadata = null;
        if (workLogDto.getFileMetadataId() != null) {
            metadata = metadataRepository.findById(workLogDto.getFileMetadataId())
                    .orElseThrow(() -> new NoSuchElementException("FileUploadMetadata not found with ID " 
                                                                + workLogDto.getFileMetadataId()));
        }

        // 2. Convert DTO -> Entity
        WorkLogEntry entry = convertToEntity(workLogDto);

        // 3. Parse the date string into LocalDate
        if (workLogDto.getDate() != null) {
            try {
                entry.setDate(LocalDate.parse(workLogDto.getDate()));
            } catch (Exception e) {
                // If date is invalid, mark validLog = false
                entry.setValidLog(false);
            }
        }

        // 4. Associate with metadata
        if (metadata != null) {
            entry.setFileUploadMetadata(metadata);

            // If we want to treat invalid logs as partial success, update metadata
            metadata.setNumberOfRecords(metadata.getNumberOfRecords() + 1);
            if (!entry.isValidLog()) {
                // If it's invalid, we might mark the metadata as PARTIAL if it was SUCCESS
                if ("SUCCESS".equals(metadata.getStatus())) {
                    metadata.setStatus("PARTIAL");
                }
            } else {
                // If this is valid, check if metadata is still "IN_PROGRESS" or "PARTIAL" and update accordingly
                // This is your domain logic. For example, if all are valid, we might do "SUCCESS",
                // but you'd have to confirm all other rows are valid, etc.
                // ? note to self: i'm happy with no else block for now, maybe add later
            }
            metadataRepository.save(metadata);
        }

        // 5. Save the WorkLogEntry
        WorkLogEntry saved = workLogEntryRepository.save(entry);
        return convertToDto(saved);
    }

    @Override
    public WorkLogDto updateWorkLog(Long id, WorkLogDto workLogDto) {
        // 1. Fetch existing entry
        WorkLogEntry existing = workLogEntryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("WorkLogEntry not found with ID " + id));

        // 2. Update fields
        if (workLogDto.getDate() != null) {
            try {
                existing.setDate(LocalDate.parse(workLogDto.getDate()));
                existing.setValidLog(true);
            } catch (Exception e) {
                existing.setValidLog(false);
            }
        }
        if (workLogDto.getEmployeeName() != null) {
            existing.setEmployeeName(workLogDto.getEmployeeName());
        }
        if (workLogDto.getProjectName() != null) {
            existing.setProjectName(workLogDto.getProjectName());
        }
        if (workLogDto.getTaskDescription() != null) {
            existing.setTaskDescription(workLogDto.getTaskDescription());
        }
        if (workLogDto.getHoursLogged() != null) {
            if (workLogDto.getHoursLogged() < 0) {
                existing.setValidLog(false);
            }
            existing.setHoursLogged(workLogDto.getHoursLogged());
        }
        // If the user explicitly sets validLog, you could override it or not
        // existing.setValidLog(workLogDto.isValidLog());

        // 3. Handle metadata changes if needed
        if (workLogDto.getFileMetadataId() != null 
                && !workLogDto.getFileMetadataId().equals(existing.getFileUploadMetadata().getId())) {
            // The user wants to move this entry to a different metadata record
            FileUploadMetadata newMetadata = metadataRepository.findById(workLogDto.getFileMetadataId())
                    .orElseThrow(() -> new NoSuchElementException("FileUploadMetadata not found with ID " 
                                                                    + workLogDto.getFileMetadataId()));
            // Decrement old metadata's record count
            // ! Note: check for status change, since we're moving a record
            FileUploadMetadata oldMetadata = existing.getFileUploadMetadata();
            oldMetadata.setNumberOfRecords(oldMetadata.getNumberOfRecords() - 1);
            metadataRepository.save(oldMetadata);

            // Associate new metadata
            // ! Note: check for status change (same as the one above)
            existing.setFileUploadMetadata(newMetadata);
            newMetadata.setNumberOfRecords(newMetadata.getNumberOfRecords() + 1);
            metadataRepository.save(newMetadata);
        }

        // 4. Save the updated entry
        WorkLogEntry updated = workLogEntryRepository.save(existing);

        return convertToDto(updated);
    }

    @Override
    public void deleteWorkLog(Long id) {
        WorkLogEntry entry = workLogEntryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("WorkLogEntry not found with ID " + id));

        // Decrement the numberOfRecords in the metadata
        FileUploadMetadata metadata = entry.getFileUploadMetadata();
        if (metadata != null) {
            metadata.setNumberOfRecords(metadata.getNumberOfRecords() - 1);
            // Possibly update the metadata status if needed
            // e.g., if all valid logs are gone, or some domain rule
            metadataRepository.save(metadata);
        }

        // Delete the entry
        workLogEntryRepository.delete(entry);
    }

    // --------------------
    // Private helper methods
    // --------------------

    private WorkLogDto convertToDto(WorkLogEntry entry) {
        WorkLogDto dto = new WorkLogDto();
        dto.setId(entry.getId());
        dto.setDate(entry.getDate() != null ? entry.getDate().toString() : null);
        dto.setEmployeeName(entry.getEmployeeName());
        dto.setProjectName(entry.getProjectName());
        dto.setTaskDescription(entry.getTaskDescription());
        dto.setHoursLogged(entry.getHoursLogged());
        dto.setValidLog(entry.isValidLog());

        if (entry.getFileUploadMetadata() != null) {
            dto.setFileMetadataId(entry.getFileUploadMetadata().getId());
        }
        return dto;
    }

    private WorkLogEntry convertToEntity(WorkLogDto dto) {
        // Basic example using ModelMapper or manual mapping
        WorkLogEntry entity = new WorkLogEntry();
        entity.setValidLog(dto.isValidLog()); 
        // We'll parse date separately in create/update methods
        entity.setEmployeeName(dto.getEmployeeName());
        entity.setProjectName(dto.getProjectName());
        entity.setTaskDescription(dto.getTaskDescription());
        entity.setHoursLogged(dto.getHoursLogged());
        return entity;
    }
}