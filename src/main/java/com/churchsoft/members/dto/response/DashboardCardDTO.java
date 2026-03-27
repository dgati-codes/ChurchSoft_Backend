package com.churchsoft.members.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardCardDTO {

    private String label;
    private Long count;
    private double percentageChange;
}