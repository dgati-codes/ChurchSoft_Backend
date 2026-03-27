package com.churchsoft.members.repo;


import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.constant.MinistryGroup;
import com.churchsoft.members.entity.Member;
import com.churchsoft.members.repo.projection.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    Optional<Member> findByMemberId(String memberId);
    Page<Member> findByStatus(MemberStatus status, Pageable pageable);


    Page<Member> findByMinistryAffiliation(MinistryAffiliation ministryAffiliation, Pageable pageable);

    List<Member> findByDateJoinedChurchBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT m FROM Member m WHERE LOWER(m.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Member> findByFullNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = 'ACTIVE'")
    long countActiveMembers();

    @Query("SELECT m.nationality, COUNT(m) FROM Member m GROUP BY m.nationality")
    List<Object[]> countMembersByNationality();

    @Query("SELECT COUNT(m) FROM Member m WHERE LOWER(m.nationality) = LOWER(:nationality)")
    Long countByNationality(String nationality);

    @Query("""
        SELECT m.ministryAffiliation, COUNT(m)
        FROM Member m
        WHERE LOWER(m.nationality) = LOWER(:nationality)
        GROUP BY m.ministryAffiliation
    """)
    List<Object[]> countByMinistryAffiliationAndNationality(String nationality);

    // Count by jurisdiction filtered by nationality
    @Query("SELECT m.jurisdiction, m.nationality, COUNT(m) FROM Member m WHERE LOWER(m.nationality) = LOWER(:nationality) GROUP BY m.jurisdiction, m.nationality")
    List<Object[]> countMembersByJurisdictionAndNationality(String nationality);

    @Query("""
    SELECT 
        COUNT(DISTINCT m.jurisdiction),
        COUNT(DISTINCT m.district),
        COUNT(DISTINCT m.assembly)
    FROM Member m
    WHERE LOWER(m.nationality) = LOWER(:nationality)
""")
    List<Object[]> countAllLevelsByNationality(String nationality);

    @Query("""
    SELECT m.assembly, m.district,
           SUM(CASE WHEN YEAR(m.createdAt) = :currentYear THEN 1 ELSE 0 END),
           SUM(CASE WHEN YEAR(m.createdAt) = :previousYear THEN 1 ELSE 0 END),
           COUNT(m)
    FROM Member m
    WHERE LOWER(m.countryOfWorship) = LOWER(:country)
    GROUP BY m.assembly, m.district
""")
    List<Object[]> getAssemblyYearlyGrowth(
            String country,
            int currentYear,
            int previousYear
    );

    @Query("SELECT DISTINCT m.assembly, m.district FROM Member m WHERE m.assembly IS NOT NULL")
    List<Object[]> findAllAssembliesWithDistrict();

    @Query("SELECT m.jurisdiction, COUNT(m) FROM Member m WHERE m.jurisdiction IS NOT NULL AND m.jurisdiction != '' GROUP BY m.jurisdiction")
    List<Object[]> findMemberCountByJurisdiction();

    @Query("SELECT COUNT(m) FROM Member m WHERE m.jurisdiction IS NOT NULL AND m.jurisdiction != ''")
    Long countMembersWithJurisdiction();

    Optional<Member> findByUserId(Long userId);



    @Query("""
    SELECT COUNT(m)
    FROM Member m
    WHERE m.status <> 'VISITOR'
    """)
    Long countAllMembers();

    @Query("""
    SELECT m FROM Member m
    WHERE EXTRACT(MONTH FROM m.dateOfBirth) = :month
    AND EXTRACT(DAY FROM m.dateOfBirth) BETWEEN :startDay AND :endDay
    """)
    List<Member> findMembersBirthdayThisWeek(
            @Param("month") int month,
            @Param("startDay") int startDay,
            @Param("endDay") int endDay
    );


    @Query("""
        SELECT m
        FROM Member m
        WHERE m.leadershipRole IS NOT NULL
        AND m.leadershipRole <> ''
    """)
    List<Member> findMembersWithLeadershipRole();

    /**
     * Count members in a ministry group
     */
    @Query("""
        SELECT COUNT(m)
        FROM Member m
        JOIN m.ministries mg
        WHERE mg = :ministry
    """)
    Long countMembersByMinistry(@Param("ministry") MinistryGroup ministry);

    /**
     * New visitors (joined within 3 months)
     */
    @Query("""
    SELECT m
    FROM Member m
    WHERE m.dateJoinedChurch >= :date
    AND m.status = 'VISITOR'
""")
    List<Member> findNewMembers(@Param("date") LocalDate date);

    @Query("""
    SELECT m FROM Member m
    WHERE m.assembly = :assembly
    AND m.leadershipRole IS NOT NULL
    AND TRIM(m.leadershipRole) <> ''
    """)
    List<Member> findMembersWithLeadershipRoleByAssembly(@Param("assembly") String assembly);

    List<Member> findByUserIdAndIsCompletedFalse(Long userId);

    /**
     * Visitors whose membership needs admin review (older than 3 months)
     */
    @Query("""
        SELECT m
        FROM Member m
        WHERE m.status = 'VISITOR'
        AND m.dateJoinedChurch <= :reviewDate
    """)
    List<Member> findVisitorsDueForReview(@Param("reviewDate") LocalDate reviewDate);


    /**
     * Count visitors requiring membership review
     */
    @Query("""
        SELECT COUNT(m)
        FROM Member m
        WHERE m.status = 'VISITOR'
        AND m.dateJoinedChurch <= :reviewDate
    """)
    Long countVisitorsDueForReview(@Param("reviewDate") LocalDate reviewDate);

    List<Member> findByCreatedByIgnoreCaseAndIsCompletedFalse(String createdBy);



    // DASHBOARD CARDS — single query
    @Query("""
    SELECT 
    m.ministryAffiliation as affiliation,
    COUNT(m) as total,
    COALESCE(SUM(CASE WHEN m.createdAt BETWEEN :start AND :end THEN 1 ELSE 0 END),0) as current,
    COALESCE(SUM(CASE WHEN m.createdAt BETWEEN :prevStart AND :prevEnd THEN 1 ELSE 0 END),0) as previous
    FROM Member m
    GROUP BY m.ministryAffiliation
    """)
    List<DashboardStatsProjection> getDashboardStats(
            LocalDateTime start,
            LocalDateTime end,
            LocalDateTime prevStart,
            LocalDateTime prevEnd
    );


    // TREND
    @Query("""
        SELECT EXTRACT(MONTH FROM m.createdAt) AS month,
               COUNT(m) AS count
        FROM Member m
        WHERE m.countryOfWorship = :country
          AND m.createdAt >= COALESCE(:startDate, m.createdAt)
          AND (:region IS NULL OR m.jurisdiction = :region)
          AND (:district IS NULL OR m.district = :district)
          AND (:local IS NULL OR m.assembly = :local)
        GROUP BY EXTRACT(MONTH FROM m.createdAt)
        ORDER BY month
    """)
    List<Object[]> getMonthlyTrend(
            @Param("startDate") LocalDateTime startDate,
            @Param("country") String country,
            @Param("region") String region,
            @Param("district") String district,
            @Param("local") String local
    );


    // AGE DISTRIBUTION
    @Query("""
    SELECT m.ministryAffiliation as affiliation,
           COUNT(m) as count
    FROM Member m
    WHERE m.countryOfWorship = :country
    GROUP BY m.ministryAffiliation
    """)
    List<AffiliationCountProjection> getAgeDistribution(String country);


    // GENDER
    @Query("""
    SELECT 
        m.ministryAffiliation as affiliation,
        m.gender as gender,
        COUNT(m) as count
    FROM Member m
    WHERE m.countryOfWorship = :country
    GROUP BY m.ministryAffiliation, m.gender
    """)
    List<GenderBreakdownProjection> getGenderBreakdown(String country);


    // REGION DISTRIBUTION
    @Query("""
    SELECT m.jurisdiction as region,
           COUNT(m) as count
    FROM Member m
    WHERE m.countryOfWorship = :country
    GROUP BY m.jurisdiction
    ORDER BY count DESC
    """)
    List<RegionDistributionProjection> getRegionDistribution(String country);

    @Query("""
    SELECT 
    COUNT(DISTINCT m.jurisdiction) as regions,
    COUNT(DISTINCT m.district) as districts,
    COUNT(DISTINCT m.assembly) as locals
    FROM Member m
    WHERE m.countryOfWorship = :country
    """)
    CoverageProjection getCoverageStats(String country);
    // LOCAL BREAKDOWN
    @Query("""
    SELECT 
        m.jurisdiction as region,
        m.district as district,
        m.assembly as assembly,
        m.ministryAffiliation as affiliation,
        COUNT(m) as count
    FROM Member m
    WHERE m.countryOfWorship = :country
    GROUP BY m.jurisdiction, m.district, m.assembly, m.ministryAffiliation
    """)
    List<LocalBreakdownProjection> getLocalBreakdown(String country);

}