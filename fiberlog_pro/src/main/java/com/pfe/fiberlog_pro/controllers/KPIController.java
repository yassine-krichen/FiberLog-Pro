package com.pfe.fiberlog_pro.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.fiberlog_pro.payloads.KPIResponseDto;
import com.pfe.fiberlog_pro.services.KPIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kpis")
@RequiredArgsConstructor
public class KPIController {

    private final KPIService kpiService;

    @GetMapping
    public ResponseEntity<KPIResponseDto> getKPIs() {
        KPIResponseDto dto = kpiService.calculateKPIs();
        return ResponseEntity.ok(dto);
    }
}