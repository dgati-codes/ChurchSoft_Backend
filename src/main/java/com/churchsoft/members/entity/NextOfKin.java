package com.churchsoft.members.entity;

import lombok.*;

import jakarta.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NextOfKin {
    private String name;
    private String relationship;
    private String contactInformation;
}