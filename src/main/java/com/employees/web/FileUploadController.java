package com.employees.web;

import com.employees.dto.EmployeeImportCSVData;
import com.employees.services.EmployeeService;
import com.employees.services.ReaderCSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private ReaderCSVService readerCSVService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employees")
    public ResponseEntity<List<Map<String, Object>>> handleEmployeesFileUpload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Map<String, Object>> employeePairsResults;

        try {
            Map<String, List<EmployeeImportCSVData>> employeeCSVData = readerCSVService.readEmployeeImportCSVData(file);
            employeePairsResults = employeeService.loadCommonWorkData(employeeCSVData);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }

        return ResponseEntity.ok(employeePairsResults);
    }


}
