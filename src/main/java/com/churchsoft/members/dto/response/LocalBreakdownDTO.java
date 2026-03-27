package com.churchsoft.members.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
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
