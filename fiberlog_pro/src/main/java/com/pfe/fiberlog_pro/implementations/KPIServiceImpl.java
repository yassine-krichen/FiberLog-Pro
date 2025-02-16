package com.pfe.fiberlog_pro.implementations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pfe.fiberlog_pro.payloads.KPIResponseDto;
import com.pfe.fiberlog_pro.repositories.WorkLogEntryRepository;
import com.pfe.fiberlog_pro.services.KPIService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KPIServiceImpl implements KPIService {

    private final WorkLogEntryRepository workLogEntryRepository;

    @Override
    public KPIResponseDto calculateKPIs() {
        KPIResponseDto dto = new KPIResponseDto();

        // 1. Total Hours Logged (only valid logs)
        Double totalHours = workLogEntryRepository.findTotalHoursLogged();
        dto.setTotalHoursLogged(totalHours != null ? totalHours : 0.0);

        // 2. Work Log Counts (total, valid, and invalid)
        Long totalLogs = workLogEntryRepository.countTotalWorkLogs();
        Long validLogs = workLogEntryRepository.countValidWorkLogs();
        Long invalidLogs = workLogEntryRepository.countInvalidWorkLogs();
        dto.setTotalWorkLogs(totalLogs != null ? totalLogs : 0L);
        dto.setValidWorkLogs(validLogs != null ? validLogs : 0L);
        dto.setInvalidWorkLogs(invalidLogs != null ? invalidLogs : 0L);

        // 3. Total Tasks Completed (assuming each valid log represents a completed task)
        dto.setTotalTasksCompleted(validLogs != null ? validLogs : 0L);

        // 4. Hours Breakdown by Project
        List<Object[]> hoursByProjectList = workLogEntryRepository.findHoursByProject();
        Map<String, Double> hoursByProject = new HashMap<>();
        for (Object[] row : hoursByProjectList) {
            String projectName = (String) row[0];
            Double hours = (Double) row[1];
            hoursByProject.put(projectName, hours);
        }
        dto.setHoursByProject(hoursByProject);

        // 5. Average Hours per Employee & Most Active Employee
        List<Object[]> hoursByEmployeeList = workLogEntryRepository.findHoursByEmployee();
        double totalEmployeeHours = 0.0;
        int employeeCount = 0;
        String mostActiveEmployee = null;
        double highestHours = 0.0;
        for (Object[] row : hoursByEmployeeList) {
            String employeeName = (String) row[0];
            Double hours = (Double) row[1];
            totalEmployeeHours += hours;
            employeeCount++;
            if (hours != null && hours > highestHours) {
                highestHours = hours;
                mostActiveEmployee = employeeName;
            }
        }
        double averageHours = employeeCount > 0 ? totalEmployeeHours / employeeCount : 0.0;
        dto.setAverageHoursPerEmployee(averageHours);
        dto.setMostActiveEmployee(mostActiveEmployee);

        return dto;
    }
}