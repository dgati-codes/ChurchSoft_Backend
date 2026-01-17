package com.churchsoft.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_record")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttendanceRecord {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate serviceDate;
    private String serviceType;

    private String submittedBy;
    private String region;
    private String district;
    private String localAssembly;

    // Demographics
    private int boys;
    private int girls;
    private int juniorYouthMale;
    private int juniorYouthFemale;
    private int seniorYouthMale;
    private int seniorYouthFemale;
    private int adultMen;
    private int adultWomen;
    private int visitorMale;
    private int visitorFemale;
    @CreationTimestamp
    private LocalDateTime createdOn;


    @OneToOne(mappedBy = "attendanceRecord", cascade = CascadeType.ALL)
    private AttendanceNote note;

}
