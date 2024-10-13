package com.employees.services;

import com.employees.dto.EmployeeImportCSVData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    public long calculateCommonDays(EmployeeImportCSVData emp1, EmployeeImportCSVData emp2) {
        LocalDate firstDateTogether = emp1.getDateFrom().isAfter(emp2.getDateFrom()) ? emp1.getDateFrom() : emp2.getDateFrom();
        LocalDate lastDateTogether = emp1.getDateTo().isBefore(emp2.getDateTo()) ? emp1.getDateTo() : emp2.getDateTo();

        if (firstDateTogether.isBefore(lastDateTogether) || firstDateTogether.isEqual(lastDateTogether)) {
            return ChronoUnit.DAYS.between(firstDateTogether, lastDateTogether);
        }

        return 0;
    }

    public List<Map<String, Object>> loadCommonWorkData(Map<String, List<EmployeeImportCSVData>> employeeCSVData) {

        Map<String, Map<String, Object>> combinedResultsMap = new HashMap<>();

        for (List<EmployeeImportCSVData> employeesOnProject : employeeCSVData.values()) {
            for (int i = 0; i < employeesOnProject.size(); i++) {
                for (int j = i + 1; j < employeesOnProject.size(); j++) {
                    EmployeeImportCSVData emp1 = employeesOnProject.get(i);
                    EmployeeImportCSVData emp2 = employeesOnProject.get(j);

                    if (isDuplicatedRecord(emp1.getId(),emp2.getId())) {
                        continue;
                    }

                    long commonDays = calculateCommonDays(emp1, emp2);
                    if (commonDays > 0) {

                        String key = generateKey(emp1.getId(), emp2.getId(), emp1.getProjectId());

                        if (combinedResultsMap.containsKey(key)) {
                            Map<String, Object> existingRecord = combinedResultsMap.get(key);
                            long existingDaysWorked = (long) existingRecord.get("daysWorked");
                            existingRecord.put("daysWorked", existingDaysWorked + commonDays);
                        } else {
                            Map<String, Object> result = new HashMap<>();
                            result.put("employeeId1", emp1.getId());
                            result.put("employeeId2", emp2.getId());
                            result.put("projectId", emp1.getProjectId());
                            result.put("daysWorked", commonDays);
                            combinedResultsMap.put(key, result);
                        }
                    }
                }
            }
        }

        List<Map<String, Object>> res = new ArrayList<>(combinedResultsMap.values());
        if (!res.isEmpty()) {
            res.sort((map1, map2) -> Long.compare((long) map2.get("daysWorked"), (long) map1.get("daysWorked")));
        }

        return res;
    }

    private boolean isDuplicatedRecord(String id1, String id2) {
        return id1.equals(id2);
    }

    private String generateKey(String empId1, String empId2, String projectId) {
        String firstId = empId1.compareTo(empId2) < 0 ? empId1 : empId2;
        String secondId = empId1.compareTo(empId2) < 0 ? empId2 : empId1;
        return firstId + "-" + secondId + "-" + projectId;
    }

}
