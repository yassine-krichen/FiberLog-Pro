package com.pfe.fiberlog_pro.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pfe.fiberlog_pro.payloads.WorkLogDto;
import com.pfe.fiberlog_pro.services.WorkLogService;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/worklogs")
@RequiredArgsConstructor
public class WorkLogController {
    private final WorkLogService workLogService;

    /**
     * GET /api/worklogs?size=10&page=0&sort=date,asc
     * Allows pagination and sorting (Spring Data format).
     */
    @GetMapping
public ResponseEntity<Page<WorkLogDto>> getAllWorkLogs(Pageable pageable) {
    Page<WorkLogDto> workLogs = workLogService.getAllWorkLogs(pageable);
    return ResponseEntity.ok(workLogs);
}

    /**
     * GET /api/worklogs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkLogDto> getWorkLogById(@PathVariable Long id) {
        WorkLogDto dto = workLogService.getWorkLogById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/worklogs
     * Creates a new work log entry.
     */
    @PostMapping
    public ResponseEntity<WorkLogDto> createWorkLog(@RequestBody WorkLogDto workLogDto) {
        WorkLogDto created = workLogService.createWorkLog(workLogDto);
        return ResponseEntity.ok(created);
    }

    /**
     * PUT /api/worklogs/{id}
     * Updates an existing work log.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkLogDto> updateWorkLog(@PathVariable Long id, @RequestBody WorkLogDto workLogDto) {
        WorkLogDto updated = workLogService.updateWorkLog(id, workLogDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/worklogs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkLog(@PathVariable Long id) {
        workLogService.deleteWorkLog(id);
        return ResponseEntity.noContent().build();
    }

    
    
}
