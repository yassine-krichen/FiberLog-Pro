package com.pfe.fiberlog_pro.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfe.fiberlog_pro.entities.WorkLogEntry;

@Repository
public interface WorkLogEntryRepository extends JpaRepository<WorkLogEntry, Long> {
    // Additional queries if needed
    @Query("SELECT SUM(e.hoursLogged) FROM WorkLogEntry e WHERE e.validLog = true")
    Double findTotalHoursLogged();

    @Query("SELECT COUNT(e) FROM WorkLogEntry e WHERE e.validLog = true")
    Long countValidWorkLogs();

    @Query("SELECT COUNT(e) FROM WorkLogEntry e")
    Long countTotalWorkLogs();

    @Query("SELECT COUNT(e) FROM WorkLogEntry e WHERE e.validLog = false")
    Long countInvalidWorkLogs();

    @Query("SELECT e.projectName, SUM(e.hoursLogged) FROM WorkLogEntry e GROUP BY e.projectName")
    List<Object[]> findHoursByProject();

    @Query("SELECT e.employeeName, SUM(e.hoursLogged) FROM WorkLogEntry e GROUP BY e.employeeName ORDER BY SUM(e.hoursLogged) DESC")
    List<Object[]> findHoursByEmployee();

}