package com.churchsoft.members.dto;

import com.churchsoft.members.constant.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MemberDTO {
    private Long id;
    private String memberId;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String hometown;
    private String nationality;
    private String ethnicity;
    private String profilePicture;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String fathersName;
    private String mothersName;
    private List<String> preferredLanguages;
    private Boolean consentForCommunication;
    private MinistryAffiliation ministryAffiliation;
    private String phoneNumber;
    private Boolean whatsappAvailable;
    private String email;
    private String physicalAddress;
    private NextOfKinDTO nextOfKin;
    private LocalDate dateJoinedChurch;
    private MemberStatus status;
    private BaptismStatus baptismStatus;
    private LocalDate baptismDate;
    private String baptismLocation;
    private BaptismType baptismType;
    private SalvationStatus salvationStatus;
    private ChurchExperienceRating churchExperienceRating;
    private EducationalLevel educationalLevel;
    private String occupation;
    private EmploymentSector employmentSector;
    private EmploymentType employmentType;
    private List<MinistryGroup> ministries;
    private String reasonForNonParticipation;
    private String leadershipRole;
    private List<String> skillsTalents;
    private List<String> spiritualGifts;
    private Boolean hasHealthIssues;
    private String specialNeedsOrMedicalConditions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}