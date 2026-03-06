package com.churchsoft.members.dto.response;

import com.churchsoft.members.constant.MinistryGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinistryLeaderDto {

    private String leaderName;
    private String leadershipRole;
    private List<MinistryGroup> ministries;
    private Long ministryMemberCount;
}