package com.churchsoft.country.repo;

import com.churchsoft.country.entity.ParentLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentLevelRepository extends JpaRepository<ParentLevel, Long> {

    @Query("""
        select distinct p.parentName
        from ParentLevel p
        where lower(p.country.countryName) = lower(:countryName)
    """)
    List<String> findParentNamesByCountry(String countryName);
}
