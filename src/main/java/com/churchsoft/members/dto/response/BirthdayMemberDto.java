package com.churchsoft.members.dto.response;

import com.churchsoft.members.constant.Gender;
import com.churchsoft.members.constant.MinistryGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthdayMemberDto {

    private String fullName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Integer ageTurning;
    private Long imageId;
    private Integer daysRemaining;
    private List<MinistryGroup> ministries;
}