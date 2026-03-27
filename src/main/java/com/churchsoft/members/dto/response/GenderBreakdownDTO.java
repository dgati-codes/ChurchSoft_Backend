package com.churchsoft.members.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenderBreakdownDTO {
    private String group;
    private Long male;
    private Long female;
}