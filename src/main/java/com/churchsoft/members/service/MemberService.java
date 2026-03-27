package com.churchsoft.members.service;


import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.dto.request.MemberCompletionDTO;
import com.churchsoft.members.dto.response.*;
import com.churchsoft.members.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
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
    List<TopGrowingAssemblyDTO> getTopGrowingAssemblies(String country, int topN);
    public List<RegionalDistributionResponse> getRegionalDistribution();
    Optional<Member> findByUserId(Long userId);
    public List<NewMemberDto> getNewMembers();
    public List<BirthdayMemberDto> getMembersBirthdayThisWeek();
    public Long getTotalMembers();
    public AssemblyLeadershipDto findMembersWithLeadershipRoleByAssembly(String assembly);
    List<NewMemberDto> getVisitorsDueForReview();
    Long getPendingVisitorReviewCount();
    void activateMember(String memberId);
    List<MemberCompletionDTO> getIncompleteMembersByCreator(String createdBy);


    List<DashboardCardDTO> getDashboardCards();

    public TrendResponseDTO getRegistrationTrend(
            LocalDateTime startDate,
            String country,
            String region,
            String district,
            String local
    );

    List<AgeDistributionDTO> getAgeDistribution(String country);

    List<GenderBreakdownDTO> getGenderBreakdown(String country);

    RegionalSummaryDTO getRegionalSummary(String country);

    List<LocalBreakdownDTO> getLocalBreakdown(String country);
    //List<InactiveLocalDTO> getInactiveLocals(String country, int topN);

}