package com.churchsoft.members.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NextOfKin {
    private String name;
    private String relationship;
    private String contactInformation;

    public int countFields() {
        return 3;
    }

    public int countFilledFields() {
        int filled = 0;
        if (name != null && !name.isBlank()) filled++;
        if (relationship != null && !relationship.isBlank()) filled++;
        if (contactInformation != null && !contactInformation.isBlank()) filled++;
        return filled;
    }
}