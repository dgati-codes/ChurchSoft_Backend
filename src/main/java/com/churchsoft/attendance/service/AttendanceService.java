package com.churchsoft.attendance.service;


import com.churchsoft.attendance.dto.AttendanceDashboardDto;
import com.churchsoft.attendance.dto.AttendanceMetricsDto;
import com.churchsoft.attendance.dto.AttendanceRecordDto;
import com.churchsoft.attendance.dto.DemographicStats;
import com.churchsoft.attendance.dto.request.AttendanceRecordRequest;
import com.churchsoft.attendance.dto.response.AttendanceMissingResponse;
import com.churchsoft.attendance.dto.response.AttendanceNoteResponse;
import com.churchsoft.attendance.dto.response.AttendanceRecordResponse;
import com.churchsoft.attendance.entity.AttendanceNote;
import com.churchsoft.attendance.entity.AttendanceRecord;
import com.churchsoft.attendance.repo.AttendanceRecordRepository;
import com.churchsoft.attendance.util.AttendanceRecordSpecs;
import com.churchsoft.global.dto.reponse.PageResult;
import com.churchsoft.global.exception.NotFoundException;
import com.churchsoft.members.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final MemberRepository memberRepository;

    public AttendanceRecordResponse createAttendance(AttendanceRecordRequest request) {
        AttendanceRecord record = mapToEntity(request);
        AttendanceRecord saved = attendanceRecordRepository.save(record);
        return toResponse(saved);
    }

    public PageResult<AttendanceRecordResponse> getAllPaginated(Pageable pageable) {
        Page<AttendanceRecord> page = attendanceRecordRepository.findAll(pageable);
        return PageResult.from(page, this::toResponse);
    }

    public Optional<AttendanceRecord> getRecord(Long id) {
        return attendanceRecordRepository.findById(id);
    }

    public void deleteRecord(Long id) {
        attendanceRecordRepository.deleteById(id);
    }

    public AttendanceRecord updateRecord(Long id, AttendanceRecordRequest request) {
        AttendanceRecord existing = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance record not found"));

        // Update core fields
        existing.setServiceDate(request.getServiceDate());
        existing.setServiceType(request.getServiceType());
        existing.setSubmittedBy(request.getSubmittedBy());
        existing.setRegion(request.getRegion());
        existing.setDistrict(request.getDistrict());
        existing.setLocalAssembly(request.getLocalAssembly());

        existing.setBoys(request.getBoys());
        existing.setGirls(request.getGirls());
        existing.setJuniorYouthMale(request.getJuniorYouthMale());
        existing.setJuniorYouthFemale(request.getJuniorYouthFemale());
        existing.setSeniorYouthMale(request.getSeniorYouthMale());
        existing.setSeniorYouthFemale(request.getSeniorYouthFemale());
        existing.setAdultMen(request.getAdultMen());
        existing.setAdultWomen(request.getAdultWomen());
        existing.setVisitorMale(request.getVisitorMale());
        existing.setVisitorFemale(request.getVisitorFemale());

        // Update note if provided
        if (request.getNote() != null) {
            AttendanceNote note = existing.getNote();
            if (note == null) {
                note = new AttendanceNote();
                note.setAttendanceRecord(existing);
                existing.setNote(note);
            }
            note.setGeneralObservations(request.getNote().getGeneralObservations());
            note.setChallengesNoticed(request.getNote().getChallengesNoticed());
            note.setRecommendation(request.getNote().getRecommendation());
        }

        return attendanceRecordRepository.save(existing);
    }

    // Mapping helpers
    private AttendanceRecord mapToEntity(AttendanceRecordRequest request) {
        AttendanceRecord record = AttendanceRecord.builder()
                .serviceDate(request.getServiceDate())
                .serviceType(request.getServiceType())
                .submittedBy(request.getSubmittedBy())
                .region(request.getRegion())
                .district(request.getDistrict())
                .localAssembly(request.getLocalAssembly())
                .boys(request.getBoys())
                .girls(request.getGirls())
                .juniorYouthMale(request.getJuniorYouthMale())
                .juniorYouthFemale(request.getJuniorYouthFemale())
                .seniorYouthMale(request.getSeniorYouthMale())
                .seniorYouthFemale(request.getSeniorYouthFemale())
                .adultMen(request.getAdultMen())
                .adultWomen(request.getAdultWomen())
                .visitorMale(request.getVisitorMale())
                .visitorFemale(request.getVisitorFemale())
                .build();

        if (request.getNote() != null) {
            AttendanceNote note = AttendanceNote.builder()
                    .generalObservations(request.getNote().getGeneralObservations())
                    .challengesNoticed(request.getNote().getChallengesNoticed())
                    .recommendation(request.getNote().getRecommendation())
                    .attendanceRecord(record)
                    .build();
            record.setNote(note);
        }
        return record;
    }

    public AttendanceRecordResponse toResponse(AttendanceRecord record) {
        return AttendanceRecordResponse.builder()
                .id(record.getId())
                .serviceDate(record.getServiceDate())
                .serviceType(record.getServiceType())
                .submittedBy(record.getSubmittedBy())
                .region(record.getRegion())
                .district(record.getDistrict())
                .localAssembly(record.getLocalAssembly())
                .boys(record.getBoys())
                .girls(record.getGirls())
                .juniorYouthMale(record.getJuniorYouthMale())
                .juniorYouthFemale(record.getJuniorYouthFemale())
                .seniorYouthMale(record.getSeniorYouthMale())
                .seniorYouthFemale(record.getSeniorYouthFemale())
                .adultMen(record.getAdultMen())
                .adultWomen(record.getAdultWomen())
                .visitorMale(record.getVisitorMale())
                .visitorFemale(record.getVisitorFemale())
                .note(record.getNote() != null ? AttendanceNoteResponse.builder()
                        .generalObservations(record.getNote().getGeneralObservations())
                        .challengesNoticed(record.getNote().getChallengesNoticed())
                        .recommendation(record.getNote().getRecommendation())
                        .build() : null)
                .build();
    }

    public AttendanceDashboardDto getDashboard(
            String region,
            String serviceType,
            String assembly,
            String district,
            LocalDate date,
            Pageable pageable
    ) {
        Specification<AttendanceRecord> spec =
                AttendanceRecordSpecs.withFilters(region, serviceType, assembly, district, date);

        // Paginated list for the table
        Page<AttendanceRecord> page = attendanceRecordRepository.findAll(spec, pageable);
        PageResult<AttendanceRecordDto> recordPage = PageResult.from(page, this::toDto);

        // Full filtered list for metrics
        List<AttendanceRecord> filteredRecords = attendanceRecordRepository.findAll(spec);
        AttendanceMetricsDto metrics = computeMetrics(filteredRecords);

        return new AttendanceDashboardDto(metrics, recordPage);
    }

    private AttendanceMetricsDto computeMetrics(List<AttendanceRecord> records) {
        int children = records.stream().mapToInt(r -> r.getBoys() + r.getGirls()).sum();
        int juniorYouth = records.stream().mapToInt(r -> r.getJuniorYouthMale() + r.getJuniorYouthFemale()).sum();
        int seniorYouth = records.stream().mapToInt(r -> r.getSeniorYouthMale() + r.getSeniorYouthFemale()).sum();
        int adults = records.stream().mapToInt(r -> r.getAdultMen() + r.getAdultWomen()).sum();
        int visitors = records.stream().mapToInt(r -> r.getVisitorMale() + r.getVisitorFemale()).sum();

        int total = children + juniorYouth + seniorYouth + adults + visitors;
        int serviceRecords = records.size();
        double average = serviceRecords == 0 ? 0 : Math.round((total * 1.0 / serviceRecords) * 100.0) / 100.0;
        int activeLocations = (int) records.stream().map(AttendanceRecord::getLocalAssembly).distinct().count();

        return new AttendanceMetricsDto(
                total,
                average,
                activeLocations,
                serviceRecords,
                new DemographicStats(children, percent(children, total)),
                new DemographicStats(juniorYouth, percent(juniorYouth, total)),
                new DemographicStats(seniorYouth, percent(seniorYouth, total)),
                new DemographicStats(adults, percent(adults, total)),
                new DemographicStats(visitors, percent(visitors, total))
        );
    }

    private double percent(int count, int total) {
        return total == 0 ? 0.0 : Math.round((count * 100.0 / total) * 100.0) / 100.0;
    }

    private AttendanceRecordDto toDto(AttendanceRecord record) {
        AttendanceNote note = record.getNote();
        return new AttendanceRecordDto(
                record.getId(),
                record.getServiceDate(),
                record.getServiceType(),
                record.getSubmittedBy(),
                record.getRegion(),
                record.getDistrict(),
                record.getLocalAssembly(),
                record.getBoys(),
                record.getGirls(),
                record.getJuniorYouthMale(),
                record.getJuniorYouthFemale(),
                record.getSeniorYouthMale(),
                record.getSeniorYouthFemale(),
                record.getAdultMen(),
                record.getAdultWomen(),
                record.getVisitorMale(),
                record.getVisitorFemale(),
                note != null ? note.getGeneralObservations() : null,
                note != null ? note.getChallengesNoticed() : null,
                note != null ? note.getRecommendation() : null
        );
    }

    public List<AttendanceMissingResponse> getAssembliesWithNoAttendance() {
        // Get all assemblies from members
        List<Object[]> allAssemblies = memberRepository.findAllAssembliesWithDistrict();

        // Get assemblies that have submitted attendance
        List<String> assembliesWithAttendance = attendanceRecordRepository.findAssembliesWithAttendance();

        // Calculate duration based on earliest service date
        LocalDate earliestDate = attendanceRecordRepository.findEarliestServiceDate();
        if (earliestDate == null) {
            earliestDate = LocalDate.now();
        }

        LocalDate today = LocalDate.now();
        long weeks = ChronoUnit.WEEKS.between(earliestDate, today);
        long months = ChronoUnit.MONTHS.between(earliestDate, today);

        String duration;
        if (weeks < 8) {
            duration = weeks + (weeks == 1 ? " week" : " weeks");
        } else {
            duration = months + (months == 1 ? " month" : " months");
        }

        // Filter assemblies with no attendance
        return allAssemblies.stream()
                .filter(assembly -> !assembliesWithAttendance.contains(assembly[0]))
                .map(assembly -> AttendanceMissingResponse.builder()
                        .assembly((String) assembly[0])
                        .district((String) assembly[1])
                        .region("") // Empty since Member doesn't have region
                        .attendanceMissingFor(duration)
                        .build())
                .collect(Collectors.toList());
    }
}
