package com.churchsoft.attendance.controller;

import com.churchsoft.attendance.dto.AttendanceDashboardDto;
import com.churchsoft.attendance.dto.request.AttendanceRecordRequest;
import com.churchsoft.attendance.dto.response.AttendanceMissingResponse;
import com.churchsoft.attendance.dto.response.AttendanceRecordResponse;
import com.churchsoft.attendance.entity.AttendanceRecord;
import com.churchsoft.attendance.service.AttendanceService;
import com.churchsoft.global.dto.reponse.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/church-soft/v1.0/attendance")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new attendance record with optional note")
    @PostMapping
    public ResponseEntity<AttendanceRecordResponse> createAttendance(
            @RequestBody AttendanceRecordRequest request) {
        AttendanceRecordResponse response = service.createAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all attendance records in a paginated list")
    @GetMapping
    public PageResult<AttendanceRecordResponse> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "serviceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return service.getAllPaginated(pageable);
    }

    @Operation(summary = "Get an attendance record by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceRecordResponse> getById(@PathVariable Long id) {
        return service.getRecord(id)
                .map(record -> ResponseEntity.ok(service.toResponse(record)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete an attendance record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update an existing attendance record by ID")
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceRecordResponse> updateRecord(
            @PathVariable Long id,
            @RequestBody AttendanceRecordRequest request) {
        AttendanceRecord updated = service.updateRecord(id, request);
        return ResponseEntity.ok(service.toResponse(updated));
    }
    @GetMapping("/metrics")
    public AttendanceDashboardDto getDashboard(
            @RequestParam(required = false, defaultValue = "ALL") String region,
            @RequestParam(required = false, defaultValue = "ALL") String serviceType,
            @RequestParam(required = false, defaultValue = "ALL") String assembly,
            @RequestParam(required = false, defaultValue = "ALL") String district,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        return service.getDashboard(region, serviceType, assembly, district, date, pageable);
    }

    @GetMapping("/inactive-locals")
    public ResponseEntity<List<AttendanceMissingResponse>> getAssembliesWithNoAttendance() {
        return ResponseEntity.ok(service.getAssembliesWithNoAttendance());
    }

}
