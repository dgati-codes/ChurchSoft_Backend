package com.churchsoft.attendance.dto;

import com.churchsoft.global.dto.reponse.PageResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDashboardDto {
    private AttendanceMetricsDto metrics;
    private PageResult<AttendanceRecordDto> attendanceRecord;
}
