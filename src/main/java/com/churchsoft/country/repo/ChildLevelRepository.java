package com.churchsoft.country.repo;

import com.churchsoft.country.entity.ChildLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildLevelRepository extends JpaRepository<ChildLevel, Long> {

    @Query("""
        select distinct c.childName
        from ChildLevel c
        where lower(c.parent.parentName) = lower(:parentName)
    """)
    List<String> findChildNamesByParent(String parentName);

    Optional<ChildLevel> findByChildNameIgnoreCase(String childName);
}