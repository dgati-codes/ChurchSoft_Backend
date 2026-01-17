package com.churchsoft.country.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(
        name = "parent_level",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"country_id", "parent_name"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String parentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private CountrySetup country;

    @OneToMany(
            mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<ChildLevel> children = new ArrayList<>();

    public void addChild(ChildLevel child) {
        child.setParent(this);
        this.children.add(child);
    }
}
