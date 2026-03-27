package com.churchsoft.members.service;

import com.churchsoft.members.constant.Gender;
import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.dto.request.MemberCompletionDTO;
import com.churchsoft.members.dto.response.*;
import com.churchsoft.members.entity.Member;
import com.churchsoft.members.repo.MemberRepository;
import com.churchsoft.members.repo.projection.*;
import com.churchsoft.members.util.DashboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.churchsoft.members.constant.MinistryAffiliation.*;


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


    private double calculateGrowth(long current, long previous) {
        if (previous == 0) return current == 0 ? 0 : 100;
        return Math.round(((current - previous) * 100.0 / previous) * 10.0) / 10.0;
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

    public Long getTotalMembers() {
        return memberRepository.countAllMembers();
    }

    public List<BirthdayMemberDto> getMembersBirthdayThisWeek() {

        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(6);

        int month = today.getMonthValue();
        int startDay = today.getDayOfMonth();
        int endDay = endOfWeek.getDayOfMonth();

        List<Member> members =
                memberRepository.findMembersBirthdayThisWeek(month, startDay, endDay);

        return members.stream()
                .map(member -> {

                    LocalDate birthdayThisYear =
                            member.getDateOfBirth().withYear(today.getYear());

                    if (birthdayThisYear.isBefore(today)) {
                        birthdayThisYear = birthdayThisYear.plusYears(1);
                    }

                    int daysRemaining =
                            (int) ChronoUnit.DAYS.between(today, birthdayThisYear);

                    int ageTurning =
                            today.getYear() - member.getDateOfBirth().getYear();

                    return BirthdayMemberDto.builder()
                            .fullName(member.getFullName())
                            .gender(member.getGender())
                            .dateOfBirth(member.getDateOfBirth())
                            .imageId(member.getImageId())
                            .daysRemaining(daysRemaining)
                            .ageTurning(ageTurning)
                            .ministries(member.getMinistries())
                            .build();
                })
                .sorted(Comparator.comparingInt(BirthdayMemberDto::getDaysRemaining))
                .toList();
    }
    
    public List<MinistryLeaderDto> getMinistryLeaders() {

        List<Member> leaders = memberRepository.findMembersWithLeadershipRole();

        return leaders.stream()
                .map(member -> {

                    Long ministryCount = 0L;

                    if (member.getMinistries() != null && !member.getMinistries().isEmpty()) {

                        ministryCount = member.getMinistries().stream()
                                .map(memberRepository::countMembersByMinistry)
                                .reduce(0L, Long::sum);
                    }

                    return MinistryLeaderDto.builder()
                            .leaderName(member.getFullName())
                            .leadershipRole(member.getLeadershipRole())
                            .ministries(member.getMinistries())
                            .ministryMemberCount(ministryCount)
                            .build();
                })
                .toList();
    }

    public List<NewMemberDto> getNewMembers() {

        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        return memberRepository.findNewMembers(threeMonthsAgo)
                .stream()
                .map(member -> NewMemberDto.builder()
                        .fullName(member.getFullName())
                        .dateJoinedChurch(member.getDateJoinedChurch())
                        .status(member.getStatus())
                        .build())
                .toList();
    }

    public AssemblyLeadershipDto findMembersWithLeadershipRoleByAssembly(String assembly) {

        List<Member> leaders =
                memberRepository.findMembersWithLeadershipRoleByAssembly(assembly);

        List<LeaderDto> leaderDtos =
                leaders.stream()
                        .map(member -> LeaderDto.builder()
                                .name(member.getFullName())
                                .leadershipRole(member.getLeadershipRole())
                                .ministries(member.getMinistries())
                                .build())
                        .toList();

        return AssemblyLeadershipDto.builder()
                .assembly(assembly)
                .leaders(leaderDtos)
                .build();
    }


    @Override
    public List<NewMemberDto> getVisitorsDueForReview() {

        LocalDate reviewDate = LocalDate.now().minusMonths(3);

        return memberRepository.findVisitorsDueForReview(reviewDate)
                .stream()
                .map(member -> NewMemberDto.builder()
                        .fullName(member.getFullName())
                        .dateJoinedChurch(member.getDateJoinedChurch())
                        .status(member.getStatus())
                        .build())
                .toList();
    }

    @Override
    public Long getPendingVisitorReviewCount() {

        LocalDate reviewDate = LocalDate.now().minusMonths(3);

        return memberRepository.countVisitorsDueForReview(reviewDate);
    }

    @Override
    @Transactional
    public void activateMember(String memberId) {

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setStatus(MemberStatus.ACTIVE);

        memberRepository.save(member);
    }

    @Override
    public List<MemberCompletionDTO> getIncompleteMembersByCreator(String createdBy) {
        List<Member> members = memberRepository.findByCreatedByIgnoreCaseAndIsCompletedFalse(createdBy);
        return members.stream()
                .map(m -> new MemberCompletionDTO(
                        m.getId(),
                        m.getFullName(),
                        m.getEmail(),
                        m.getCreatedAt(),
                        m.getUpdatedAt(),
                        m.getMemberId(),
                        m.getIsCompleted(),
                        m.getCompletionPercentageWeighted()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<DashboardCardDTO> getDashboardCards() {

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.atTime(23,59,59);

        LocalDate lastMonth = today.minusMonths(1);
        LocalDateTime prevStart = lastMonth.withDayOfMonth(1).atStartOfDay();
        LocalDateTime prevEnd = lastMonth.atTime(23,59,59);

        List<DashboardStatsProjection> stats =
                memberRepository.getDashboardStats(start, end, prevStart, prevEnd);

        Map<MinistryAffiliation, DashboardStatsProjection> map =
                stats.stream().collect(Collectors.toMap(
                        DashboardStatsProjection::getAffiliation,
                        s -> s
                ));

        return List.of(
                buildCard("Children", CHILDREN, map),
                buildCard("Junior Youth", JUNIOR_YOUTH, map),
                buildCard("Senior Youth", SENIOR_YOUTH, map),
                buildAdultsCard(map),
                buildTotalCard(stats)
        );
    }

    private DashboardCardDTO buildCard(String label,
                                       MinistryAffiliation affiliation,
                                       Map<MinistryAffiliation, DashboardStatsProjection> map) {

        DashboardStatsProjection s = map.get(affiliation);

        long total = s == null ? 0 : safe(s.getTotal());
        long current = s == null ? 0 : safe(s.getCurrent());
        long previous = s == null ? 0 : safe(s.getPrevious());

        return DashboardCardDTO.builder()
                .label(label)
                .count(total)
                .percentageChange(calculatePercentageChange(current, previous))
                .build();
    }

    private DashboardCardDTO buildAdultsCard(
            Map<MinistryAffiliation, DashboardStatsProjection> map) {

        DashboardStatsProjection men = map.get(MEN);
        DashboardStatsProjection women = map.get(WOMEN);

        long total =
                (men == null ? 0 : men.getTotal()) +
                        (women == null ? 0 : women.getTotal());

        long current =
                (men == null ? 0 : men.getCurrent()) +
                        (women == null ? 0 : women.getCurrent());

        long previous =
                (men == null ? 0 : men.getPrevious()) +
                        (women == null ? 0 : women.getPrevious());

        return DashboardCardDTO.builder()
                .label("Adults")
                .count(total)
                .percentageChange(calculatePercentageChange(current, previous))
                .build();
    }

    private DashboardCardDTO buildTotalCard(List<DashboardStatsProjection> stats) {

        long total = stats.stream()
                .mapToLong(s -> safe(s.getTotal()))
                .sum();

        long current = stats.stream()
                .mapToLong(s -> safe(s.getCurrent()))
                .sum();

        long previous = stats.stream()
                .mapToLong(s -> safe(s.getPrevious()))
                .sum();

        return DashboardCardDTO.builder()
                .label("Total Members")
                .count(total)
                .percentageChange(calculatePercentageChange(current, previous))
                .build();
    }

    private double calculatePercentageChange(long current, long previous) {
        if (previous == 0) return current == 0 ? 0 : 100;
        return ((double) (current - previous) / previous) * 100;
    }

    private long safe(Long v) {
        return v == null ? 0 : v;
    }



    @Override
    public TrendResponseDTO getRegistrationTrend(
            LocalDateTime startDate,
            String country,
            String region,
            String district,
            String local
    ) {

        // start of current year if not provided
        if (startDate == null) {
            startDate = LocalDate.now()
                    .withDayOfYear(1)
                    .atStartOfDay();
        }

        List<Object[]> results = memberRepository
                .getMonthlyTrend(startDate, country, region, district, local);

        Map<Integer, Long> map = results.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> (Long) r[1]
                ));

        List<MonthlyTrendDTO> monthly = new ArrayList<>();
        long total = 0;

        for (int i = 1; i <= 12; i++) {
            long count = map.getOrDefault(i, 0L);
            total += count;

            monthly.add(MonthlyTrendDTO.builder()
                    .month(Month.of(i).name().substring(0, 3))
                    .count(count)
                    .build());
        }

        return new TrendResponseDTO(monthly, total);
    }

    @Override
    public List<AgeDistributionDTO> getAgeDistribution(String country) {

        Map<MinistryAffiliation, Long> map =
                memberRepository.getAgeDistribution(country)
                        .stream()
                        .collect(Collectors.toMap(
                                AffiliationCountProjection::getAffiliation,
                                AffiliationCountProjection::getCount
                        ));

        return List.of(
                new AgeDistributionDTO("Children", safe(map.get(CHILDREN))),
                new AgeDistributionDTO("Junior Youth", safe(map.get(JUNIOR_YOUTH))),
                new AgeDistributionDTO("Senior Youth", safe(map.get(SENIOR_YOUTH))),
                new AgeDistributionDTO("Adults",
                        safe(map.get(MEN)) + safe(map.get(WOMEN)))
        );
    }


    @Override
    public List<GenderBreakdownDTO> getGenderBreakdown(String country) {

        Map<String, GenderBreakdownDTO> map = initGenderGroups();

        for (GenderBreakdownProjection r :
                memberRepository.getGenderBreakdown(country)) {

            String group = DashboardUtils.mapAffiliationToGroup(r.getAffiliation());
            GenderBreakdownDTO dto = map.get(group);

            if (r.getGender() == Gender.MALE)
                dto.setMale(dto.getMale() + r.getCount());
            else
                dto.setFemale(dto.getFemale() + r.getCount());
        }

        return new ArrayList<>(map.values());
    }

    private Map<String, GenderBreakdownDTO> initGenderGroups() {

        Map<String, GenderBreakdownDTO> map = new LinkedHashMap<>();

        map.put("Children", new GenderBreakdownDTO("Children", 0L, 0L));
        map.put("Junior Youth", new GenderBreakdownDTO("Junior Youth", 0L, 0L));
        map.put("Senior Youth", new GenderBreakdownDTO("Senior Youth", 0L, 0L));
        map.put("Adults", new GenderBreakdownDTO("Adults", 0L, 0L));

        return map;
    }

    @Override
    public RegionalSummaryDTO getRegionalSummary(String country) {

        List<MemberDistributionDto> regions = memberRepository.getRegionDistribution(country)
                .stream()
                .map(r -> MemberDistributionDto.builder()
                        .region(r.getRegion())
                        .count(r.getCount())
                        .build())
                .toList();

        CoverageProjection result = memberRepository.getCoverageStats(country);

        CoverageDTO coverage = CoverageDTO.builder()
                .regions(safe(result.getRegions()))
                .districts(safe(result.getDistricts()))
                .locals(safe(result.getLocals()))
                .build();

        return RegionalSummaryDTO.builder()
                .regions(regions)
                .coverage(coverage)
                .build();
    }

    @Override
    public List<LocalBreakdownDTO> getLocalBreakdown(String country) {

        Map<String, LocalBreakdownDTO> grouped = new HashMap<>();

        for (LocalBreakdownProjection row :
                memberRepository.getLocalBreakdown(country)) {

            String key = row.getRegion()+"|"+row.getDistrict()+"|"+row.getAssembly();

            grouped.putIfAbsent(key,
                    LocalBreakdownDTO.builder()
                            .region(row.getRegion())
                            .district(row.getDistrict())
                            .local(row.getAssembly())
                            .children(0L)
                            .juniorYouth(0L)
                            .seniorYouth(0L)
                            .adults(0L)
                            .total(0L)
                            .build()
            );

            LocalBreakdownDTO dto = grouped.get(key);

            switch (row.getAffiliation()) {
                case CHILDREN -> dto.setChildren(dto.getChildren()+row.getCount());
                case JUNIOR_YOUTH -> dto.setJuniorYouth(dto.getJuniorYouth()+row.getCount());
                case SENIOR_YOUTH -> dto.setSeniorYouth(dto.getSeniorYouth()+row.getCount());
                case MEN, WOMEN -> dto.setAdults(dto.getAdults()+row.getCount());
            }

            dto.setTotal(dto.getTotal()+row.getCount());
        }

        return new ArrayList<>(grouped.values());
    }

    @Override
    public List<TopGrowingAssemblyDTO> getTopGrowingAssemblies(String country, int topN) {

        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;

        List<Object[]> results = memberRepository.getAssemblyYearlyGrowth(
                country, currentYear, previousYear
        );

        return results.stream()
                .map(r -> {

                    String assembly = (String) r[0];
                    String district = (String) r[1];

                    long current = ((Number) r[2]).longValue();
                    long previous = ((Number) r[3]).longValue();
                    long total = ((Number) r[4]).longValue();

                    double growth = calculateGrowth(current, previous);

                    return TopGrowingAssemblyDTO.builder()
                            .assembly(assembly)
                            .district(district)
                            .totalMembers(total)
                            .growthPercentage(growth)
                            .build();
                })
                .sorted(Comparator.comparing(TopGrowingAssemblyDTO::getGrowthPercentage).reversed())
                .limit(topN)
                .toList();
    }

   /* @Override
    public List<InactiveLocalDTO> getInactiveLocals(String country, int topN) {

        List<Object[]> results = memberRepository.getAssemblyLastActivity(country);

        LocalDateTime now = LocalDateTime.now();

        return results.stream()
                .map(r -> {

                    String assembly = (String) r[0];
                    String district = (String) r[1];
                    long total = ((Number) r[2]).longValue();
                    LocalDateTime lastActivity = (LocalDateTime) r[3];

                    long monthsInactive = ChronoUnit.MONTHS.between(lastActivity, now);

                    return InactiveLocalDTO.builder()
                            .assembly(assembly)
                            .district(district)
                            .totalMembers(total)
                            .lastActivityDate(lastActivity)
                            .monthsInactive(monthsInactive)
                            .build();
                })
                .sorted(Comparator.comparing(InactiveLocalDTO::getMonthsInactive).reversed())
                .limit(topN)
                .toList();
    }*/






}