package com.churchsoft.country.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentLevelDto {
    private String parentName;
    private List<ChildLevelDto> children = new ArrayList<>();
}
