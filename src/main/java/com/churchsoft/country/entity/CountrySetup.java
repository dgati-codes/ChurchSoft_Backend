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
        name = "country_setup",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "country_name")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountrySetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String countryName;

    private String description;

    private String parentLevel;
    private String childLevel;

    @OneToMany(
            mappedBy = "country",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<ParentLevel> parents = new ArrayList<>();

    public void addParent(ParentLevel parent) {
        parent.setCountry(this);
        this.parents.add(parent);
    }
}
