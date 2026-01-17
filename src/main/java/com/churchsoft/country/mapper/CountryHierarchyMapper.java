package com.churchsoft.country.mapper;

import com.churchsoft.country.dto.ChildLevelDto;
import com.churchsoft.country.dto.CountryHierarchyDto;
import com.churchsoft.country.dto.ParentLevelDto;
import com.churchsoft.country.entity.ChildLevel;
import com.churchsoft.country.entity.CountrySetup;
import com.churchsoft.country.entity.ParentLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CountryHierarchyMapper {

    // Entity -> DTO
    public static CountryHierarchyDto toDto(CountrySetup entity) {
        List<ParentLevelDto> parents = new ArrayList<>();
        if (entity.getParents() != null) {
            for (ParentLevel p : entity.getParents()) {
                parents.add(toDtoParent(p));
            }
        }

        return CountryHierarchyDto.builder()
                .countryName(entity.getCountryName())
                .description(entity.getDescription())
                .parentLevel(entity.getParentLevel())
                .childLevel(entity.getChildLevel())
                .parents(parents)
                .build();
    }

    private static ParentLevelDto toDtoParent(ParentLevel entity) {
        List<ChildLevelDto> children = new ArrayList<>();
        if (entity.getChildren() != null) {
            for (ChildLevel c : entity.getChildren()) {
                children.add(toDtoChild(c));
            }
        }

        return ParentLevelDto.builder()
                .parentName(entity.getParentName())
                .children(children)
                .build();
    }

    private static ChildLevelDto toDtoChild(ChildLevel entity) {
        List<String> grandChildrenList = new ArrayList<>();
        if (entity.getGrandChildren() != null && !entity.getGrandChildren().isBlank()) {
            grandChildrenList = Arrays.stream(entity.getGrandChildren().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        return ChildLevelDto.builder()
                .childName(entity.getChildName())
                .grandChildren(grandChildrenList)
                .build();
    }

    // DTO -> Entity
    public static CountrySetup toEntity(CountryHierarchyDto dto) {
        CountrySetup country = CountrySetup.builder()
                .countryName(dto.getCountryName())
                .description(dto.getDescription())
                .parentLevel(dto.getParentLevel())
                .childLevel(dto.getChildLevel())
                .parents(new ArrayList<>())
                .build();

        if (dto.getParents() != null) {
            for (ParentLevelDto p : dto.getParents()) {
                ParentLevel parent = ParentLevel.builder()
                        .parentName(p.getParentName())
                        .children(new ArrayList<>())
                        .country(country)
                        .build();

                if (p.getChildren() != null) {
                    for (ChildLevelDto c : p.getChildren()) {
                        ChildLevel child = ChildLevel.builder()
                                .childName(c.getChildName())
                                .grandChildren(String.join(", ", c.getGrandChildren()))
                                .parent(parent)
                                .build();
                        parent.getChildren().add(child);
                    }
                }

                country.getParents().add(parent);
            }
        }

        return country;
    }
}
