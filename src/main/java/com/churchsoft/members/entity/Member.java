package com.churchsoft.members.entity;

import com.churchsoft.members.constant.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Table(
        name = "members",
        indexes = {
                @Index(name = "idx_member_created_at", columnList = "created_at"),
                @Index(name = "idx_member_ministry", columnList = "ministryAffiliation"),
                @Index(name = "idx_member_gender", columnList = "gender"),
                @Index(name = "idx_member_region", columnList = "jurisdiction"),
                @Index(name = "idx_member_district", columnList = "district"),
                @Index(name = "idx_member_local", columnList = "assembly"),
                @Index(name = "idx_member_country", columnList = "country_of_worship"),
                @Index(
                        name="idx_member_dashboard",
                        columnList="country_of_worship,created_at,jurisdiction,district,assembly"
                )
        }
)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "member_id", unique = true, nullable = false)
    private String memberId;

    @Column(name = "image_id")
    private Long imageId;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "country_of_worship")
    private String countryOfWorship;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private String hometown;
    private String nationality;
    private String jurisdiction;
    private String district;
    private String assembly;
    private String ethnicity;

    @Column(name = "profile_picture_url")
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    private IdentificationType identificationType;

    private String identificationNumber;

    private String fathersName;
    private String mothersName;

    @ElementCollection
    @CollectionTable(name = "member_preferred_languages", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "language")
    private List<String> preferredLanguages;

    private Boolean consentForCommunication;

    @Enumerated(EnumType.STRING)
    private MinistryAffiliation ministryAffiliation;

    // Contact & Location Details
    private String phoneNumber;
    private Boolean whatsappAvailable;
    private String email;
    private String physicalAddress;

    @Embedded
    private NextOfKin nextOfKin;

    // Spiritual Journey
    private LocalDate dateJoinedChurch;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private BaptismStatus baptismStatus;

    private LocalDate baptismDate;
    private String baptismLocation;

    @Enumerated(EnumType.STRING)
    private BaptismType baptismType;

    @Enumerated(EnumType.STRING)
    private SalvationStatus salvationStatus;

    @Enumerated(EnumType.STRING)
    private ChurchExperienceRating churchExperienceRating;

    // Education & Occupation
    @Enumerated(EnumType.STRING)
    private EducationalLevel educationalLevel;

    private String occupation;

    @Enumerated(EnumType.STRING)
    private EmploymentSector employmentSector;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    // Ministry & Skills
    @ElementCollection
    @CollectionTable(name = "member_ministries", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    private List<MinistryGroup> ministries;

    private String reasonForNonParticipation;
    private String leadershipRole;

    @ElementCollection
    @CollectionTable(name = "member_skills", joinColumns = @JoinColumn(name = "member_id"))
    private List<String> skillsTalents;

    @ElementCollection
    @CollectionTable(name = "member_spiritual_gifts", joinColumns = @JoinColumn(name = "member_id"))
    private List<String> spiritualGifts;

    // Welfare & Health
    private Boolean hasHealthIssues;
    private String specialNeedsOrMedicalConditions;

    // System
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String createdBy;
    private Boolean isCompleted = false;
    private Integer page = 1;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (memberId == null) {
            memberId = "CH" + (System.currentTimeMillis() % 1000000) + "-" + UUID.randomUUID().toString().substring(0, 4);
        }
        this.isCompleted = calculateCompletionStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        this.isCompleted = calculateCompletionStatus();
    }

    // ---------------------------------------------
    // Weighted Completion Methods
    // ---------------------------------------------
    @Transient
    public double calculateCompletionRatioWeighted() {

        double filledWeight = 0;
        double totalWeight = 0;

        // Core fields (weight 3)
        filledWeight += (fullName != null && !fullName.isBlank() ? 3 : 0);
        filledWeight += (dateOfBirth != null ? 3 : 0);
        filledWeight += (gender != null ? 3 : 0);
        filledWeight += (status != null ? 3 : 0);
        totalWeight += 12;

        // Contact info (weight 3)
        filledWeight += (phoneNumber != null && !phoneNumber.isBlank() ? 3 : 0);
        filledWeight += (email != null && !email.isBlank() ? 3 : 0);
        filledWeight += (physicalAddress != null && !physicalAddress.isBlank() ? 3 : 0);
        totalWeight += 9;

        // Church journey (weight 2)
        filledWeight += (dateJoinedChurch != null ? 2 : 0);
        filledWeight += (baptismStatus != null ? 2 : 0);
        totalWeight += 4;

        // Education & occupation (weight 1)
        filledWeight += (occupation != null && !occupation.isBlank() ? 1 : 0);
        filledWeight += (educationalLevel != null ? 1 : 0);
        filledWeight += (ministryAffiliation != null ? 1 : 0);
        totalWeight += 3;

        // Embedded NextOfKin (weight 2)
        if (nextOfKin != null) {
            int nextFilled = nextOfKin.countFilledFields();
            int nextTotal = nextOfKin.countFields();
            filledWeight += ((double) nextFilled / nextTotal) * 2;
        }
        totalWeight += 2;

        // Collections (weight 1 each)
        List<List<?>> collections = Arrays.asList(preferredLanguages, ministries, skillsTalents, spiritualGifts);
        for (List<?> col : collections) {
            totalWeight += 1;
            if (col != null && !col.isEmpty()) filledWeight += 1;
        }

        // Minor optional fields (weight 0.5)
        List<Object> minorFields = Arrays.asList(
                fathersName, mothersName, ethnicity, identificationType, identificationNumber,
                profilePicture, district, assembly, jurisdiction, countryOfWorship
        );
        for (Object f : minorFields) {
            totalWeight += 0.5;
            if (f != null && (!(f instanceof String) || !((String) f).isBlank())) filledWeight += 0.5;
        }

        // Boolean fields (weight 0.5)
        List<Boolean> booleans = Arrays.asList(hasHealthIssues, consentForCommunication, whatsappAvailable);
        for (Boolean b : booleans) {
            totalWeight += 0.5;
            if (b != null) filledWeight += 0.5;
        }

        return totalWeight == 0 ? 0 : filledWeight / totalWeight;
    }

    @Transient
    public int getCompletionPercentageWeighted() {
        return (int) Math.round(calculateCompletionRatioWeighted() * 100);
    }

    private boolean calculateCompletionStatus() {
        return calculateCompletionRatioWeighted() >= 0.8;
    }
}