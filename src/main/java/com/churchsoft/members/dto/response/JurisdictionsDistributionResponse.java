package com.churchsoft.members.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class JurisdictionsDistributionResponse {
    private List<Map<String, Object>> jurisdictionsDistribution;
}