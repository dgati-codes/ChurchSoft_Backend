package com.churchsoft.members.dto.response;

import com.churchsoft.members.constant.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewMemberDto {

    private String fullName;
    private LocalDate dateJoinedChurch;
    private MemberStatus status;
}