package com.churchsoft.members.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopGrowingAssemblyDTO {

    private String assembly;
    private String district;
    private Long totalMembers;

    private Double growthPercentage;
}