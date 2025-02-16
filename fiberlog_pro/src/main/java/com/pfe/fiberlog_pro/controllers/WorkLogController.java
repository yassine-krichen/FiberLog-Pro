package com.pfe.fiberlog_pro.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pfe.fiberlog_pro.payloads.WorkLogDto;
import com.pfe.fiberlog_pro.services.WorkLogService;


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
    public ResponseEntity<Page<WorkLogDto>> getAllWorkLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        // Parse sort parameter
        // e.g., sort=field,asc or sort=field,desc
        // You can have multiple sorts, so parse them accordingly
        // ! Note: sorting is not functional, please fix it (I don't have time for this shit)
        Sort sortSpec = parseSort(sort);

        PageRequest pageRequest = PageRequest.of(page, size, sortSpec);
        Page<WorkLogDto> workLogs = workLogService.getAllWorkLogs(pageRequest);
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

    // --------------------
    // Helper method to parse the "sort" request param
    // --------------------
    private Sort parseSort(String[] sort) {
        // Example: sort=field,asc or sort=field,desc
        // If multiple sorts, you can chain them.
        // For simplicity, let's assume only one param, e.g. "date,asc"
        if (sort.length == 1) {
            String[] parts = sort[0].split(",");
            if (parts.length == 2) {
                String field = parts[0];
                String direction = parts[1];
                return "desc".equalsIgnoreCase(direction)
                        ? Sort.by(field).descending()
                        : Sort.by(field).ascending();
            }
        }
        // Default fallback
        return Sort.by("id").ascending();
    }
    
}
