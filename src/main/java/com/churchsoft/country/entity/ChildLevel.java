package com.churchsoft.country.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = "child_level",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"parent_id", "child_name"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String childName;

    @Column(length = 2000)
    private String grandChildren; // comma-separated

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private ParentLevel parent;
}
