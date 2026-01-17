package com.churchsoft.members.repo;


import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    // Count total members in the country for a given nationality
    @Query("SELECT COUNT(m) FROM Member m WHERE LOWER(m.nationality) = LOWER(:nationality)")
    long countTotalByNationality(@Param("nationality") String nationality);

    @Query("SELECT m.assembly AS assembly, m.district AS district, COUNT(m) AS count " +
            "FROM Member m " +
            "WHERE LOWER(m.nationality) = LOWER(:nationality) " +
            "GROUP BY m.assembly, m.district " +
            "ORDER BY COUNT(m) DESC")
    List<Object[]> countByAssemblyForNationalityWithDistrict(@Param("nationality") String nationality);

    @Query("SELECT DISTINCT m.assembly, m.district FROM Member m WHERE m.assembly IS NOT NULL")
    List<Object[]> findAllAssembliesWithDistrict();

    @Query("SELECT m.jurisdiction, COUNT(m) FROM Member m WHERE m.jurisdiction IS NOT NULL AND m.jurisdiction != '' GROUP BY m.jurisdiction")
    List<Object[]> findMemberCountByJurisdiction();

    @Query("SELECT COUNT(m) FROM Member m WHERE m.jurisdiction IS NOT NULL AND m.jurisdiction != ''")
    Long countMembersWithJurisdiction();

    Optional<Member> findByUserId(Long userId);
}