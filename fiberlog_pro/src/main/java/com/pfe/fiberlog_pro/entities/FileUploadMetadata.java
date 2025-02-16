package com.pfe.fiberlog_pro.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file_upload_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime uploadDate;

    private String status; // e.g. SUCCESS, FAILED, etc.

    private Integer numberOfRecords;

    /**
     * The relationship: One FileUploadMetadata -> Many WorkLogEntries
     */
    @OneToMany(
        mappedBy = "fileUploadMetadata", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<WorkLogEntry> workLogEntries = new ArrayList<>();
}

