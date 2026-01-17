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
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "members")
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
    // Personal & Identity Information
    @Column(nullable = false)
    private String fullName;

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

    // Spiritual Journey & Church Membership
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

    // Ministry Involvement & Skills
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

    // Welfare & Health Information
    private Boolean hasHealthIssues;
    private String specialNeedsOrMedicalConditions;

    // Administrative & System Data
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

    /**
     * Determine if member record is 70% complete
     */
    private boolean calculateCompletionStatus() {
        List<Object> fieldsToCheck = Arrays.asList(
                fullName, dateOfBirth, gender, maritalStatus, hometown, nationality,
                phoneNumber, email, physicalAddress, status, educationalLevel,
                occupation, baptismStatus, salvationStatus, ministryAffiliation
        );

        long filled = fieldsToCheck.stream()
                .filter(Objects::nonNull)
                .filter(f -> !(f instanceof String) || !((String) f).isBlank())
                .count();

        double ratio = (double) filled / fieldsToCheck.size();
        return ratio >= 0.7;
    }
}
