package com.churchsoft.attendance.util;

import com.churchsoft.attendance.entity.AttendanceRecord;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRecordSpecs {

    public static Specification<AttendanceRecord> withFilters(
            String region,
            String serviceType,
            String assembly,
            String district,
            LocalDate date
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Region
            if (region != null && !region.isBlank() && !"ALL".equalsIgnoreCase(region)) {
                predicates.add(cb.equal(cb.lower(root.get("region")), region.toLowerCase()));
            }

            // Service Type (Enum)
            if (serviceType != null && !serviceType.isBlank() && !"ALL".equalsIgnoreCase(serviceType)) {
                try {
                    predicates.add(cb.equal(root.get("serviceType"), serviceType.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    // Ignore invalid serviceType values
                }
            }

            // Assembly
            if (assembly != null && !assembly.isBlank() && !"ALL".equalsIgnoreCase(assembly)) {
                predicates.add(cb.equal(cb.lower(root.get("localAssembly")), assembly.toLowerCase()));
            }

            // District
            if (district != null && !district.isBlank() && !"ALL".equalsIgnoreCase(district)) {
                predicates.add(cb.equal(cb.lower(root.get("district")), district.toLowerCase()));
            }

            // Date
            if (date != null) {
                predicates.add(cb.equal(root.get("serviceDate"), date));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
