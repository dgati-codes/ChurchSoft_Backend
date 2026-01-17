package com.churchsoft.country.service;

import com.churchsoft.country.dto.CountryHierarchyDto;
import com.churchsoft.country.entity.ChildLevel;
import com.churchsoft.country.entity.CountrySetup;
import com.churchsoft.country.entity.ParentLevel;
import com.churchsoft.country.mapper.CountryHierarchyMapper;
import com.churchsoft.country.repo.ChildLevelRepository;
import com.churchsoft.country.repo.CountrySetupRepository;
import com.churchsoft.country.repo.ParentLevelRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountrySetupService {

    private final CountrySetupRepository repository;
    private final CountrySetupRepository countryRepo;
    private final ParentLevelRepository parentRepo;
    private final ChildLevelRepository childRepo;

    /**  Fetch all country names */
    public List<String> fetchAllCountries() {
        return countryRepo.findAllCountryNames();
    }

    /** Fetch parent names by country */
    public List<String> fetchParentsByCountry(String countryName) {
        return parentRepo.findParentNamesByCountry(countryName);
    }

    /** Fetch child names by parent */
    public List<String> fetchChildrenByParent(String parentName) {
        return childRepo.findChildNamesByParent(parentName);
    }

    /** Fetch grandChildren by child */
    public List<String> fetchGrandChildrenByChild(String childName) {
        ChildLevel child = childRepo.findByChildNameIgnoreCase(childName)
                .orElseThrow(() ->
                        new RuntimeException("Child not found: " + childName));

        if (child.getGrandChildren() == null || child.getGrandChildren().isBlank()) {
            return List.of();
        }

        return Arrays.stream(child.getGrandChildren().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
    @Transactional
    public CountrySetup save(CountrySetup incoming) {

        CountrySetup country = repository
                .findByCountryNameIgnoreCase(incoming.getCountryName())
                .orElseGet(() -> CountrySetup.builder()
                        .countryName(incoming.getCountryName().trim())
                        .description(incoming.getDescription())
                        .parentLevel(incoming.getParentLevel())
                        .childLevel(incoming.getChildLevel())
                        .parents(new ArrayList<>())
                        .build()
                );

        for (ParentLevel p : incoming.getParents()) {

            ParentLevel parent = country.getParents().stream()
                    .filter(x -> x.getParentName().equalsIgnoreCase(p.getParentName()))
                    .findFirst()
                    .orElseGet(() -> {
                        ParentLevel np = ParentLevel.builder()
                                .parentName(p.getParentName().trim())
                                .children(new ArrayList<>())
                                .build();
                        country.addParent(np);
                        return np;
                    });

            for (ChildLevel c : p.getChildren()) {

                ChildLevel child = parent.getChildren().stream()
                        .filter(x -> x.getChildName().equalsIgnoreCase(c.getChildName()))
                        .findFirst()
                        .orElseGet(() -> {
                            ChildLevel nc = ChildLevel.builder()
                                    .childName(c.getChildName().trim())
                                    .grandChildren("")
                                    .build();
                            parent.addChild(nc);
                            return nc;
                        });

                mergeGrandChildren(child, c.getGrandChildren());
            }
        }

        return repository.save(country);
    }

    public List<CountrySetup> findAll() {
        return repository.findAll();
    }

    public CountrySetup findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country setup not found"));
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public CountryHierarchyDto fetchByCountry(@PathVariable String countryName) {
        return CountryHierarchyMapper.toDto(
                repository.findByCountryNameIgnoreCase(countryName)
                        .orElseThrow(() -> new RuntimeException("Country not found"))
        );
    }


    @Transactional
    public void importExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean firstRow = true;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (firstRow) {
                    firstRow = false; // skip header
                    continue;
                }

                String countryName = getCellValue(row.getCell(0));
                String parentLevel = getCellValue(row.getCell(1));
                String childLevel = getCellValue(row.getCell(2));
                String parentName = getCellValue(row.getCell(4));
                String childName = getCellValue(row.getCell(5));
                String grandChildren = getCellValue(row.getCell(6));

                if (countryName.isEmpty() || parentName.isEmpty() || childName.isEmpty()) continue;

                CountrySetup setup = repository.findByCountryNameIgnoreCase(countryName)
                        .orElseGet(() -> repository.save(
                                CountrySetup.builder()
                                        .countryName(countryName)
                                        .description("Imported from Excel")
                                        .parentLevel(parentLevel)
                                        .childLevel(childLevel)
                                        .parents(new ArrayList<>())
                                        .build()
                        ));

                ParentLevel parent = setup.getParents().stream()
                        .filter(p -> p.getParentName().equalsIgnoreCase(parentName))
                        .findFirst()
                        .orElseGet(() -> {
                            ParentLevel newParent = ParentLevel.builder()
                                    .parentName(parentName)
                                    .children(new ArrayList<>())
                                    .country(setup)
                                    .build();
                            setup.getParents().add(newParent);
                            return newParent;
                        });

                ChildLevel child = parent.getChildren().stream()
                        .filter(c -> c.getChildName().equalsIgnoreCase(childName))
                        .findFirst()
                        .orElseGet(() -> {
                            ChildLevel newChild = ChildLevel.builder()
                                    .childName(childName)
                                    .grandChildren("")
                                    .parent(parent)
                                    .build();
                            parent.getChildren().add(newChild);
                            return newChild;
                        });

                Set<String> existing = new HashSet<>();
                if (child.getGrandChildren() != null && !child.getGrandChildren().isBlank()) {
                    existing.addAll(Arrays.stream(child.getGrandChildren().split(","))
                            .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet()));
                }

                Set<String> newEntries = Arrays.stream(grandChildren.split(","))
                        .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());

                existing.addAll(newEntries);
                child.setGrandChildren(String.join(", ", existing));
            }

            repository.saveAll(repository.findAll());

        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file", e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
        return "";
    }

    public List<CountryHierarchyDto> fetchAll() {
        List<CountrySetup> countries = repository.findAll();
        List<CountryHierarchyDto> dtos = new ArrayList<>();
        for (CountrySetup c : countries) {
            dtos.add(CountryHierarchyMapper.toDto(c));
        }
        return dtos;
    }

    public CountryHierarchyDto fetchByCountryName(String countryName) {
        CountrySetup country = repository.findByCountryNameIgnoreCase(countryName)
                .orElseThrow(() -> new RuntimeException("Country not found: " + countryName));
        return CountryHierarchyMapper.toDto(country);
    }


    ///  Helper Method
    private void mergeGrandChildren(ChildLevel child, String incoming) {

        Set<String> merged = new LinkedHashSet<>();

        if (child.getGrandChildren() != null) {
            Arrays.stream(child.getGrandChildren().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(merged::add);
        }

        if (incoming != null) {
            Arrays.stream(incoming.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(merged::add);
        }

        child.setGrandChildren(String.join(", ", merged));
    }

}
