package com.churchsoft.members.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionalSummaryDTO {

    private List<MemberDistributionDto> regions;
    private CoverageDTO coverage;
}