package com.churchsoft.country.repo;


import com.churchsoft.country.entity.CountrySetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountrySetupRepository extends JpaRepository<CountrySetup, Long> {
    Optional<CountrySetup> findByCountryNameIgnoreCase(String countryName);

    @Query("select distinct c.countryName from CountrySetup c")
    List<String> findAllCountryNames();
}