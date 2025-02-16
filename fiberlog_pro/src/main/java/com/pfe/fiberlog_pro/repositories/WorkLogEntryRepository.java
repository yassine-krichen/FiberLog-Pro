package com.pfe.fiberlog_pro.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfe.fiberlog_pro.entities.WorkLogEntry;

@Repository
public interface WorkLogEntryRepository extends JpaRepository<WorkLogEntry, Long> {
    // Additional queries if needed
}