package com.churchsoft.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceMetricsDto {
    private int totalAttendance;
    private double averageAttendance;
    private int activeLocations;
    private int serviceRecords;

    private DemographicStats children;
    private DemographicStats juniorYouth;
    private DemographicStats seniorYouth;
    private DemographicStats adults;
    private DemographicStats visitors;
}
