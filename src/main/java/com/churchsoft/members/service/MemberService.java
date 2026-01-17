package com.churchsoft.members.service;


import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.dto.response.JurisdictionsDistributionResponse;
import com.churchsoft.members.dto.response.NationalitySummaryResponse;
import com.churchsoft.members.dto.response.RegionalDistributionResponse;
import com.churchsoft.members.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemberService {
    Member createMember(Member member);
    Optional<Member> getMemberById(Long id);
    Optional<Member> getMemberByMemberId(String memberId);
    Page<Member> getAllMembers(Pageable pageable);
    Member updateMember(Member memberDetails);
    void deleteMember(Long id);
    List<Member> searchMembers(String searchTerm);
    Page<Member> getMembersByStatus(MemberStatus status, Pageable pageable);
    Page<Member> getMembersByMinistry(MinistryAffiliation ministry, Pageable pageable);
    long getActiveMembersCount();
    Member updateMemberStatus(String memberId, String status);
    Member updateMemberPage(String memberId, Integer page);
    public NationalitySummaryResponse getSummaryByNationality(String nationality);
    public JurisdictionsDistributionResponse getJurisdictionDistributionByNationality(String nationality);
    public Map<String, Object> getTotalCoverageByNationality(String nationality);
    public List<Map<String, Object>> getTopAssembliesByNationality(String nationality, int topN);
    public List<RegionalDistributionResponse> getRegionalDistribution();
    Optional<Member> findByUserId(Long userId);

}