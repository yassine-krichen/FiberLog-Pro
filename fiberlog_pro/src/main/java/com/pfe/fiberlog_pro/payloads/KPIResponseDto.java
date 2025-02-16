package com.pfe.fiberlog_pro.payloads;

import lombok.Data;
import java.util.Map;

@Data
public class KPIResponseDto {
    private Double totalHoursLogged;          // Sum of valid hours logged
    private Double averageHoursPerEmployee;     // Average of total hours per employee
    private Long totalTasksCompleted;           // Number of valid logs (tasks completed)
    private Map<String, Double> hoursByProject; // Breakdown: project name → total hours
    private Long totalWorkLogs;                 // Total number of work log entries
    private Long validWorkLogs;                 // Number of valid work logs
    private Long invalidWorkLogs;               // Number of invalid work logs
    private String mostActiveEmployee;          // Employee with the highest hours logged
}