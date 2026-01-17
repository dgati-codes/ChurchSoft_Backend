package com.churchsoft.attendance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceMissingResponse {
    private String assembly;
    private String district;
    private String region;
    private String attendanceMissingFor; // e.g., "6 weeks", "10 months", "2 years", "No attendance ever submitted"
}