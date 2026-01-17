package com.churchsoft.attendance.repo;

import com.churchsoft.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long>, JpaSpecificationExecutor<AttendanceRecord> {

    @Query("SELECT DISTINCT a.localAssembly FROM AttendanceRecord a WHERE a.localAssembly IS NOT NULL")
    List<String> findAssembliesWithAttendance();

    @Query("SELECT MIN(a.serviceDate) FROM AttendanceRecord a")
    LocalDate findEarliestServiceDate();
}

