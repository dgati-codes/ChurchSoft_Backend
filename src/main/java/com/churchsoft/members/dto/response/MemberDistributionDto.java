package com.churchsoft.members.dto.response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDistributionDto {
    private String region;
    private Long count;
}
