package com.churchsoft.attendance.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordResponse {
    private Long id;
    private LocalDate serviceDate;
    private String serviceType;
    private String submittedBy;
    private String region;
    private String district;
    private String localAssembly;

    private int boys;
    private int girls;
    private int juniorYouthMale;
    private int juniorYouthFemale;
    private int seniorYouthMale;
    private int seniorYouthFemale;
    private int adultMen;
    private int adultWomen;
    private int visitorMale;
    private int visitorFemale;

    private AttendanceNoteResponse note;
}
