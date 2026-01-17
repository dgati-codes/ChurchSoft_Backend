package com.churchsoft.members.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class NationalitySummaryResponse {
    private String nationality;
    private Long totalMembers;
    private List<Map<String, Long>> ministryAffiliationCounts;
}
