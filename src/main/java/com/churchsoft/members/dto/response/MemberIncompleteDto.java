package com.churchsoft.members.dto.response;

import com.churchsoft.members.constant.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberIncompleteDto {

    private Long id;
    private String fullName;
    private Integer page;
    private MemberStatus status;
    private int completionPercentage;
}