package com.churchsoft.members.service;

import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.dto.response.JurisdictionsDistributionResponse;
import com.churchsoft.members.dto.response.NationalitySummaryResponse;
import com.churchsoft.members.dto.response.RegionalDistributionResponse;
import com.churchsoft.members.entity.Member;
import com.churchsoft.members.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member createMember(Member member) {

        log.info("Creating new member: {}", member.getFullName());
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> getMemberById(Long id) {
        log.debug("Fetching member by id: {}", id);
        return memberRepository.findById(id);
    }
    public Optional<Member> findByUserId(Long id) {
        return Optional.ofNullable(memberRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Member Not Found using the user Id " + id)));
    }

    @Override
    public Optional<Member> getMemberByMemberId(String memberId) {
        log.debug("Fetching member by memberId: {}", memberId);
        return memberRepository.findByMemberId(memberId);
    }

    @Override
    public Page<Member> getAllMembers(Pageable pageable) {
        log.debug("Fetching all members with pagination");
        return memberRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Member updateMember( Member memberDetails) {
        log.info("Updating member with id: {}", memberDetails.getId());

        Member existingMember = memberRepository.findById(memberDetails.getId())
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberDetails.getId()));

        // Copy updatable fields
        existingMember.setFullName(memberDetails.getFullName());
        existingMember.setUserId(memberDetails.getUserId());
        existingMember.setImageId(memberDetails.getImageId());
        existingMember.setDateOfBirth(memberDetails.getDateOfBirth());
        existingMember.setGender(memberDetails.getGender());
        existingMember.setMaritalStatus(memberDetails.getMaritalStatus());
        existingMember.setHometown(memberDetails.getHometown());
        existingMember.setNationality(memberDetails.getNationality());
        existingMember.setJurisdiction(memberDetails.getJurisdiction());
        existingMember.setDistrict(memberDetails.getDistrict());
        existingMember.setAssembly(memberDetails.getAssembly());
        existingMember.setEthnicity(memberDetails.getEthnicity());
        existingMember.setProfilePicture(memberDetails.getProfilePicture());
        existingMember.setIdentificationType(memberDetails.getIdentificationType());
        existingMember.setIdentificationNumber(memberDetails.getIdentificationNumber());
        existingMember.setFathersName(memberDetails.getFathersName());
        existingMember.setMothersName(memberDetails.getMothersName());
        existingMember.setPreferredLanguages(memberDetails.getPreferredLanguages());
        existingMember.setConsentForCommunication(memberDetails.getConsentForCommunication());
        existingMember.setMinistryAffiliation(memberDetails.getMinistryAffiliation());
        existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        existingMember.setWhatsappAvailable(memberDetails.getWhatsappAvailable());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhysicalAddress(memberDetails.getPhysicalAddress());
        existingMember.setNextOfKin(memberDetails.getNextOfKin());
        existingMember.setDateJoinedChurch(memberDetails.getDateJoinedChurch());
        existingMember.setStatus(memberDetails.getStatus());
        existingMember.setBaptismStatus(memberDetails.getBaptismStatus());
        existingMember.setBaptismDate(memberDetails.getBaptismDate());
        existingMember.setBaptismLocation(memberDetails.getBaptismLocation());
        existingMember.setBaptismType(memberDetails.getBaptismType());
        existingMember.setSalvationStatus(memberDetails.getSalvationStatus());
        existingMember.setChurchExperienceRating(memberDetails.getChurchExperienceRating());
        existingMember.setEducationalLevel(memberDetails.getEducationalLevel());
        existingMember.setOccupation(memberDetails.getOccupation());
        existingMember.setEmploymentSector(memberDetails.getEmploymentSector());
        existingMember.setEmploymentType(memberDetails.getEmploymentType());
        existingMember.setMinistries(memberDetails.getMinistries());
        existingMember.setReasonForNonParticipation(memberDetails.getReasonForNonParticipation());
        existingMember.setLeadershipRole(memberDetails.getLeadershipRole());
        existingMember.setSkillsTalents(memberDetails.getSkillsTalents());
        existingMember.setSpiritualGifts(memberDetails.getSpiritualGifts());
        existingMember.setHasHealthIssues(memberDetails.getHasHealthIssues());
        existingMember.setSpecialNeedsOrMedicalConditions(memberDetails.getSpecialNeedsOrMedicalConditions());

        // --- Page update logic ---
        int currentPage = existingMember.getPage() != null ? existingMember.getPage() : 1;
        Integer requestedPage = memberDetails.getPage();


        if (requestedPage != null) {
            if (requestedPage >= 7) {
                // Lock page at 7 and mark complete
                existingMember.setPage(7);
                existingMember.setIsCompleted(true);
            } else {
                // Otherwise, increment by 1
                existingMember.setPage(requestedPage + 1);
            }
        } else {
            // Fallback: ensure it doesn’t exceed 7
            if (currentPage < 7) {
                existingMember.setPage(currentPage + 1);
            } else {
                existingMember.setPage(7);
                existingMember.setIsCompleted(true);
            }
        }

        return memberRepository.save(existingMember);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        log.info("Deleting member with id: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        memberRepository.delete(member);
    }

    @Override
    public List<Member> searchMembers(String searchTerm) {
        log.debug("Searching members with term: {}", searchTerm);
        return memberRepository.findByFullNameContainingIgnoreCase(searchTerm);
    }

    @Override
    public Page<Member> getMembersByStatus(MemberStatus status, Pageable pageable) {
        log.debug("Fetching members by status: {}", status);
        return memberRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Member> getMembersByMinistry(MinistryAffiliation ministry, Pageable pageable) {
        log.debug("Fetching members by ministry: {}", ministry);
        return memberRepository.findByMinistryAffiliation(ministry, pageable);
    }

    @Override
    public long getActiveMembersCount() {
        log.debug("Fetching active members count");
        return memberRepository.countActiveMembers();
    }

    @Override
    @Transactional
    public Member updateMemberStatus(String memberId, String status) {
        log.info("Updating member status for {} to {}", memberId, status);
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with memberId: " + memberId));

        member.setStatus(MemberStatus.valueOf(status.toUpperCase()));
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member updateMemberPage(String memberId, Integer page) {
        log.info("Updating member page for {} to {}", memberId, page);
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with memberId: " + memberId));
        member.setPage(page);
        return memberRepository.save(member);
    }

    public NationalitySummaryResponse getSummaryByNationality(String nationality) {
        Long totalMembers = memberRepository.countByNationality(nationality);

        List<Object[]> results = memberRepository.countByMinistryAffiliationAndNationality(nationality);
        List<Map<String, Long>> ministryCounts = new ArrayList<>();

        for (Object[] row : results) {
            String affiliation = row[0] != null ? row[0].toString() : "UNASSIGNED";
            Long count = (Long) row[1];
            ministryCounts.add(Map.of(affiliation, count));
        }

        return new NationalitySummaryResponse(nationality, totalMembers, ministryCounts);
    }


    public JurisdictionsDistributionResponse getJurisdictionDistributionByNationality(String nationality) {
        // Total members for grand total of this nationality
        Long grandTotal = memberRepository.countByNationality(nationality);

        // Total members per jurisdiction for this nationality
        List<Object[]> results = memberRepository.countMembersByJurisdictionAndNationality(nationality);

        List<Map<String, Object>> distribution = new ArrayList<>();

        // Add nationality as the first entry
        distribution.add(Map.of("nationality", nationality));

        // Add jurisdictions
        for (Object[] row : results) {
            String jurisdiction = row[0] != null ? row[0].toString() : "UNASSIGNED";
            Long totalMembers = (Long) row[2]; // row[2] is count
            double percentage = grandTotal > 0 ? (totalMembers * 100.0) / grandTotal : 0.0;

            Map<String, Object> jurisdictionMap = new LinkedHashMap<>();
            jurisdictionMap.put("jurisdiction", jurisdiction);
            jurisdictionMap.put("totalMembers", totalMembers);
            jurisdictionMap.put("percentage", Math.round(percentage * 100.0) / 100.0);

            distribution.add(jurisdictionMap);
        }

        return new JurisdictionsDistributionResponse(distribution);
    }

    public Map<String, Object> getTotalCoverageByNationality(String nationality) {
        List<Object[]> resultList = memberRepository.countAllLevelsByNationality(nationality);

        Object[] counts = resultList.isEmpty() ? new Object[]{0L, 0L, 0L} : resultList.get(0);

        Long jurisdictionCount = counts[0] != null ? ((Number) counts[0]).longValue() : 0L;
        Long districtCount = counts[1] != null ? ((Number) counts[1]).longValue() : 0L;
        Long localAssemblyCount = counts[2] != null ? ((Number) counts[2]).longValue() : 0L;

        Map<String, Object> totalCoverage = new HashMap<>();
        totalCoverage.put("jurisdictionCount", jurisdictionCount);
        totalCoverage.put("districtCount", districtCount);
        totalCoverage.put("localAssemblyCount", localAssemblyCount);

        Map<String, Object> response = new HashMap<>();
        response.put("totalCoverage", totalCoverage);

        return response;
    }

    public List<Map<String, Object>> getTopAssembliesByNationality(String nationality, int topN) {
        long totalNationalityCount = memberRepository.countTotalByNationality(nationality);
        List<Object[]> results = memberRepository.countByAssemblyForNationalityWithDistrict(nationality);

        return results.stream()
                .limit(topN)
                .map(r -> {
                    String assembly = (String) r[0];
                    String district = (String) r[1];
                    long count = ((Number) r[2]).longValue();
                    double percentage = totalNationalityCount == 0 ? 0 : (count * 100.0 / totalNationalityCount);

                    Map<String, Object> map = new HashMap<>();
                    map.put("assembly", assembly);
                    map.put("district", district);
                    map.put("count", count);
                    map.put("percentage", Math.round(percentage * 100.0) / 100.0); // 2 decimals
                    return map;
                })
                .toList();
    }

    public List<RegionalDistributionResponse> getRegionalDistribution() {
        // Get member count by jurisdiction
        List<Object[]> jurisdictionCounts = memberRepository.findMemberCountByJurisdiction();

        // Get total members with jurisdiction data
        Long totalMembers = memberRepository.countMembersWithJurisdiction();

        if (totalMembers == 0) {
            return Collections.emptyList();
        }

        return jurisdictionCounts.stream()
                .map(result -> {
                    String jurisdiction = (String) result[0];
                    Long count = (Long) result[1];
                    double percentage = (count.doubleValue() / totalMembers) * 100;

                    return RegionalDistributionResponse.builder()
                            .region(jurisdiction) // Using jurisdiction as region
                            .memberCount(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0) // Round to 2 decimal places
                            .build();
                })
                .sorted((r1, r2) -> r2.getMemberCount().compareTo(r1.getMemberCount())) // Sort by count descending
                .collect(Collectors.toList());
    }

}