package com.churchsoft.members.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionalDistributionDTO {
    private String region;
    private Long children;
    private Long juniorYouth;
    private Long seniorYouth;
    private Long adults;
    private Long total;
    private Long male;
    private Long female;
}