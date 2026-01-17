package com.churchsoft.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendance_note")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttendanceNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "attendance_record_id")
    private AttendanceRecord attendanceRecord;

    @Column(length = 1000)
    private String generalObservations;

    @Column(length = 1000)
    private String challengesNoticed;

    @Column(length = 1000)
    private String recommendation;

}
