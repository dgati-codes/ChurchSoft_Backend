package com.churchsoft.country.controller;

import com.churchsoft.country.dto.CountryHierarchyDto;
import com.churchsoft.country.entity.CountrySetup;
import com.churchsoft.country.mapper.CountryHierarchyMapper;
import com.churchsoft.country.service.CountrySetupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/church-soft/v1.0/country-setup")
@RequiredArgsConstructor
public class CountrySetupController {

    private final CountrySetupService service;

    /** Fetch all country names */
    @Operation(summary = "Fetch all country names")
    @GetMapping("/countries")
    public ResponseEntity<List<String>> getAllCountries() {
        return ResponseEntity.ok(service.fetchAllCountries());
    }

    /**  Fetch all parent names by country */
    @Operation(summary = "Fetch parent(Regions) levels by country name")
    @GetMapping("/countries/parents/{countryName}")
    public ResponseEntity<List<String>> getParentsByCountry(
            @PathVariable String countryName) {

        return ResponseEntity.ok(
                service.fetchParentsByCountry(countryName)
        );
    }

    /**Fetch all child names by parent name */
    @Operation(summary = "Fetch child levels(Districts) by parent name")
    @GetMapping("/children/{parentName}")
    public ResponseEntity<List<String>> getChildrenByParent(
            @PathVariable String parentName) {

        return ResponseEntity.ok(
                service.fetchChildrenByParent(parentName)
        );
    }

    /** Fetch grandChildren by child name */
    @Operation(summary = "Fetch grand children(local Assemblies) by child name(District)")
    @GetMapping("/grandchildren/{childName}")
    public ResponseEntity<List<String>> getGrandChildrenByChild(
            @PathVariable String childName) {

        return ResponseEntity.ok(
                service.fetchGrandChildrenByChild(childName)
        );
    }
    @Operation(summary = "Create or update country setup")
    @PostMapping
    public ResponseEntity<CountryHierarchyDto> createOrUpdate(@RequestBody CountryHierarchyDto dto) {
        CountrySetup entity = CountryHierarchyMapper.toEntity(dto);
        CountrySetup saved = service.save(entity);
        return ResponseEntity.ok(CountryHierarchyMapper.toDto(saved));
    }


    @Operation(summary = "Import country setup from Excel")
    @PostMapping(value = "/import",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        service.importExcel(file);
        return ResponseEntity.ok("Excel imported successfully");
    }


    @Operation(summary = "Fetch all country hierarchies")
    @GetMapping("/hierarchy")
    public List<CountryHierarchyDto> fetchAll() {
        return service.findAll()
                .stream()
                .map(CountryHierarchyMapper::toDto)
                .toList();
    }

    @GetMapping("/hierarchy/{countryName}")
    public CountryHierarchyDto fetchByCountry(@PathVariable String countryName) {
        return service.fetchByCountry(countryName);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a country setup by ID",
            description = """
                    Deletes a country administrative configuration using its unique ID.
                    
                    This operation will also delete all related:
                    - Parent administrative levels
                    - Child administrative levels
                    
                    Example hierarchy removed:
                    
                    Country
                        → Region/State (ParentLevel)
                                → District/City (ChildLevel)
                    
                    This action is irreversible.
                    """
    )
    public ResponseEntity<String> deleteCountryById(@PathVariable Long id) {

       service.deleteById(id);

        return ResponseEntity.ok("Country setup deleted successfully");
    }

    @DeleteMapping("/name/{countryName}")
    @Operation(
            summary = "Delete a country setup by country name",
            description = """
                    Deletes a country administrative configuration using the country name.
                 
                    """
    )
    public ResponseEntity<String> deleteCountryByName(
            @PathVariable String countryName
    ) {

        service.deleteByCountryName(countryName);

        return ResponseEntity.ok("Country setup deleted successfully");
    }

}
