package com.pfe.fiberlog_pro.implementations;



import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfe.fiberlog_pro.entities.FileUploadMetadata;
import com.pfe.fiberlog_pro.entities.WorkLogEntry;
import com.pfe.fiberlog_pro.payloads.FileUploadResponseDto;
import com.pfe.fiberlog_pro.payloads.WorkLogRowDto;
import com.pfe.fiberlog_pro.repositories.FileUploadMetadataRepository;
import com.pfe.fiberlog_pro.repositories.WorkLogEntryRepository;
import com.pfe.fiberlog_pro.services.FileUploadService;

import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadMetadataRepository metadataRepository;
    private final WorkLogEntryRepository workLogEntryRepository;
    private final ModelMapper modelMapper;

    @Override
    public FileUploadResponseDto processFile(MultipartFile file) {
        FileUploadResponseDto responseDto = new FileUploadResponseDto();
        List<String> errors = new ArrayList<>();
        List<WorkLogEntry> entriesToSave = new ArrayList<>();

        // 1. Validate file type or extension
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".csv") && !fileName.endsWith(".xlsx"))) {
            // Invalid file type
            responseDto.setStatus("FAILED");
            responseDto.setMessage("Invalid file type. Only .csv or .xlsx supported.");
            return responseDto;
        }

        // 2. Create FileUploadMetadata
        FileUploadMetadata metadata = FileUploadMetadata.builder()
                .fileName(fileName)
                .uploadDate(LocalDateTime.now())
                .status("IN_PROGRESS")
                .numberOfRecords(0)
                .build();

        // We'll save metadata at the end, or in a try-catch block
        metadataRepository.save(metadata);

        // 3. Parse file
        boolean isCsv = fileName.endsWith(".csv");
        List<WorkLogRowDto> rowDtos;
        try {
            rowDtos = isCsv ? parseCsv(file) : parseExcel(file);
        } catch (Exception e) {
            // File is corrupted or unreadable
            responseDto.setStatus("FAILED");
            responseDto.setMessage("Failed to parse file: " + e.getMessage());
            // revert the metadata if needed or mark as failed
            metadata.setStatus("FAILED");
            metadataRepository.save(metadata);
            return responseDto;
        }

        // 4. Validate & Convert each row
        int invalidCount = 0;
        for (int i = 0; i < rowDtos.size(); i++) {
            WorkLogRowDto rowDto = rowDtos.get(i);
            boolean valid = validateRowDto(rowDto, errors, i);

            // Convert DTO -> Entity
            WorkLogEntry entry = modelMapper.map(rowDto, WorkLogEntry.class);

            // If date is a String, we need to convert to LocalDate
            // because modelMapper won't do that automatically
            try {
                entry.setDate(LocalDate.parse(rowDto.getDate()));
            } catch (Exception e) {
                valid = false;
                errors.add("Row " + (i + 1) + ": Invalid date format -> " + rowDto.getDate());
            }

            entry.setValidLog(valid);  // Mark if row is valid
            entry.setFileUploadMetadata(metadata);

            if (!valid) {
                invalidCount++;
            }

            entriesToSave.add(entry);
        }

        // 5. Persist the entries (partial or full)
        // Because of cascade or direct saving, we can either:
        // A) Save metadata with the attached entries
        // or B) Save them separately
        // Here, let's just set them and re-save metadata

        metadata.setWorkLogEntries(entriesToSave);
        metadata.setNumberOfRecords(rowDtos.size());

        // Decide final status
        if (invalidCount == rowDtos.size()) {
            // All invalid
            metadata.setStatus("FAILED");
            responseDto.setStatus("FAILED");
            responseDto.setMessage("All rows were invalid. No valid records saved.");
        } else if (invalidCount > 0) {
            metadata.setStatus("PARTIAL");
            responseDto.setStatus("PARTIAL");
            responseDto.setMessage("Some rows were invalid. Partial success.");
        } else {
            metadata.setStatus("SUCCESS");
            responseDto.setStatus("SUCCESS");
            responseDto.setMessage("All rows processed successfully.");
        }

        metadataRepository.save(metadata); // This will cascade to entries if set up

        // 6. Build response
        responseDto.setErrors(errors);
        responseDto.setTotalRecordsProcessed(rowDtos.size());
        responseDto.setInvalidRecordsCount(invalidCount);

        return responseDto;
    }

    /**
     * Parse CSV file into a list of row DTOs.
     */
    private List<WorkLogRowDto> parseCsv(MultipartFile file) throws Exception {
        List<WorkLogRowDto> rowDtos = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            @SuppressWarnings("deprecation")
            CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            for (CSVRecord record : csvParser) {
                WorkLogRowDto dto = new WorkLogRowDto();
                dto.setDate(record.get("Date"));
                dto.setEmployeeName(record.get("EmployeeName"));
                dto.setProjectName(record.get("ProjectName"));
                dto.setTaskDescription(record.get("TaskDescription"));
                dto.setHoursLogged(Double.valueOf(record.get("HoursLogged")));
                rowDtos.add(dto);
            }
        }
        return rowDtos;
    }

    /**
     * Parse Excel (.xlsx) file into a list of row DTOs.
     */
    private List<WorkLogRowDto> parseExcel(MultipartFile file) throws Exception {
        List<WorkLogRowDto> rowDtos = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming first sheet
            // Assuming the first row is a header row
            int rowCount = sheet.getPhysicalNumberOfRows();
            // Find column indices by header name if needed, or just do a fixed order

            Row headerRow = sheet.getRow(0);
            int dateCol = -1, empCol = -1, projCol = -1, taskCol = -1, hoursCol = -1;
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                String cellVal = headerRow.getCell(i).getStringCellValue();
                switch (cellVal) {
                    case "Date": dateCol = i; break;
                    case "EmployeeName": empCol = i; break;
                    case "ProjectName": projCol = i; break;
                    case "TaskDescription": taskCol = i; break;
                    case "HoursLogged": hoursCol = i; break;
                    // add more columns if needed
                }
            }

            for (int r = 1; r < rowCount; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                
                WorkLogRowDto dto = new WorkLogRowDto();
                dto.setDate(getCellValue(row.getCell(dateCol)));
                dto.setEmployeeName(getCellValue(row.getCell(empCol)));
                dto.setProjectName(getCellValue(row.getCell(projCol)));
                dto.setTaskDescription(getCellValue(row.getCell(taskCol)));
                dto.setHoursLogged(Double.valueOf(getCellValue(row.getCell(hoursCol))));
                
                rowDtos.add(dto);
            }
        }
        return rowDtos;
    }

    private String getCellValue(Cell cell) {
    if (cell == null) return "";
    switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue().trim();

        case NUMERIC:
            // Check if this numeric cell is actually a date
            if (DateUtil.isCellDateFormatted(cell)) {
                // Extract the date from the cell
                LocalDate localDate = cell.getDateCellValue()
                                          .toInstant()
                                          .atZone(ZoneId.systemDefault())
                                          .toLocalDate();
                // Return the date as an ISO-8601 string, e.g. "2025-02-10"
                return localDate.toString();
            } else {
                // It's a plain number
                return String.valueOf(cell.getNumericCellValue());
            }

        case BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());

        default:
            return "";
    }
}


    /**
     * Validate the row DTO fields.
     * Returns true if valid, false otherwise.
     */
    private boolean validateRowDto(WorkLogRowDto dto, List<String> errors, int rowIndex) {
        boolean valid = true;
        // Basic checks
        if (dto.getDate() == null || dto.getDate().isEmpty()) {
            errors.add("Row " + (rowIndex + 1) + ": Missing date.");
            valid = false;
        }
        if (dto.getEmployeeName() == null || dto.getEmployeeName().isEmpty()) {
            errors.add("Row " + (rowIndex + 1) + ": Missing employee name.");
            valid = false;
        }
        if (dto.getHoursLogged() == null || dto.getHoursLogged() < 0) {
            errors.add("Row " + (rowIndex + 1) + ": Invalid hours logged.");
            valid = false;
        }
        // Add more domain-specific validations as needed
        return valid;
    }
}
