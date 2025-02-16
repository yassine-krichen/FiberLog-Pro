package com.pfe.fiberlog_pro.implementations;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.pfe.fiberlog_pro.payloads.KPIResponseDto;
import com.pfe.fiberlog_pro.payloads.WorkLogDto;
import com.pfe.fiberlog_pro.services.KPIService;
import com.pfe.fiberlog_pro.services.ReportService;
import com.pfe.fiberlog_pro.services.WorkLogService;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final @Lazy KPIService kpiService;
    private final @Lazy WorkLogService workLogService;

    @Override
    public byte[] generatePdfReport() {
        // Retrieve KPI data and a sample (first 10 work logs)
        KPIResponseDto kpi = kpiService.calculateKPIs();
        List<WorkLogDto> sampleWorkLogs = workLogService
                .getAllWorkLogs(PageRequest.of(0, 10))
                .getContent();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float currentY = yStart;

                // Title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("FiberLog Pro KPI Report");
                contentStream.endText();

                currentY -= 30;

                // KPI Summary
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Total Hours Logged: " + kpi.getTotalHoursLogged());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Average Hours per Employee: " + kpi.getAverageHoursPerEmployee());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Total Tasks Completed: " + kpi.getTotalTasksCompleted());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Total Work Logs: " + kpi.getTotalWorkLogs());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Valid Work Logs: " + kpi.getValidWorkLogs());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Invalid Work Logs: " + kpi.getInvalidWorkLogs());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Most Active Employee: " + kpi.getMostActiveEmployee());
                contentStream.endText();

                currentY -= 15 * 7 + 20;

                // Sample Work Logs Header
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Sample Work Logs:");
                contentStream.endText();

                currentY -= 20;

                // Table Header (simplified, one line per work log)
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("ID | Date       | Employee       | Project          | Hours | Valid");
                contentStream.endText();

                currentY -= 15;

                // Sample Data Rows
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(margin, currentY);
                for (WorkLogDto log : sampleWorkLogs) {
                    String line = String.format("%d | %s | %-14s | %-16s | %.2f | %s",
                            log.getId(),
                            log.getDate(),
                            log.getEmployeeName(),
                            log.getProjectName(),
                            log.getHoursLogged(),
                            log.isValidLog());
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                    currentY -= 15;
                    // (For multi-page support, check currentY and create a new page if needed)
                }
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}