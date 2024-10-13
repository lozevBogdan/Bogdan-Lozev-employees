package com.employees.services;

import com.employees.dto.EmployeeImportCSVData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReaderCSVService {
    @Autowired
    private DateService dateService;

    public Map<String, List<EmployeeImportCSVData>> readEmployeeImportCSVData(MultipartFile file) throws IOException {

        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

        Map<String, List<EmployeeImportCSVData>> res = new HashMap<>();
        Set<String> readRows = new HashSet<>();

        for (CSVRecord record : csvParser) {
            String empId = record.get("EmpID");
            String projectId = record.get("ProjectID");
            LocalDate dateFrom = dateService.parseStringToLocalDate(record.get("DateFrom"));
            LocalDate dateTo =
                    (record.get("DateTo") == null || record.get("DateTo").trim().equals(""))
                            ? LocalDate.now()
                            : dateService.parseStringToLocalDate(record.get("DateTo"));

            if (isNotPresent(empId) || isNotPresent(projectId)) {
                continue;
            }

            String row = generateRowKey(empId, projectId, dateFrom, dateTo);
            if (isDuplicatedRow(readRows,row)) {
                continue;
            }

            readRows.add(row);

            EmployeeImportCSVData employeeProject = new EmployeeImportCSVData(empId, projectId, dateFrom, dateTo);

            res.computeIfAbsent(projectId, k -> new ArrayList<>()).add(employeeProject);
        }

        return res;
    }

    private boolean isDuplicatedRow(Set<String> readRows, String row) {
        return readRows.contains(row);
    }

    private String generateRowKey(String empId, String projectId, LocalDate dateFrom, LocalDate dateTo) {
        return empId + "-" + projectId + "-" + dateFrom.toString() + "-" + dateTo.toString();
    }

    private boolean isNotPresent(String input) {
        return input == null || input.trim().equals("");
    }
}
