package com.pfe.fiberlog_pro.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_log_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String employeeName;

    private String projectName;

    @Column(length = 500)
    private String taskDescription;

    private Double hoursLogged;

    // New field to mark validity
    private boolean validLog;  // true if valid, false if any validation fails

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * The inverse side of the One-to-Many relationship
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_upload_metadata_id")
    private FileUploadMetadata fileUploadMetadata;
}
