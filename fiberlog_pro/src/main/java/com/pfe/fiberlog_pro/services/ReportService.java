package com.pfe.fiberlog_pro.services;

public interface ReportService {
    /**
     * Generates a PDF report summarizing KPIs and a sample of work log entries.
     *
     * @return the PDF content as a byte array
     */
    byte[] generatePdfReport();
}