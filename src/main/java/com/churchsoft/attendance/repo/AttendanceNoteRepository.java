package com.churchsoft.attendance.repo;

import com.churchsoft.attendance.entity.AttendanceNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceNoteRepository extends JpaRepository<AttendanceNote, Long> {}