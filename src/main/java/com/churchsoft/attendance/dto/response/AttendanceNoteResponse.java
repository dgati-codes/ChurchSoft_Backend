package com.churchsoft.attendance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceNoteResponse {
    private String generalObservations;
    private String challengesNoticed;
    private String recommendation;
}
