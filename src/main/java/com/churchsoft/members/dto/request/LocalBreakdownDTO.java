package com.churchsoft.members.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalBreakdownDTO {

    private String region;
    private String district;
    private String local;

    private Long children;
    private Long juniorYouth;
    private Long seniorYouth;
    private Long adults;
    private Long total;
}