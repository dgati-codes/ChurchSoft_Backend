package com.churchsoft.members.dto.request;

import com.churchsoft.members.dto.NextOfKinDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateMemberRequest {

    // Personal & Identity Information
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String hometown;
    private String nationality;
    private String jurisdiction;
    private String district;
    private String assembly;
    private String ethnicity;
    private String profilePicture;
    private String identificationType;
    private String identificationNumber;
    private String fathersName;
    private String mothersName;
    private List<String> preferredLanguages;
    private Boolean consentForCommunication;
    private String ministryAffiliation;

    // Contact & Location Details
    private String phoneNumber;
    private Boolean whatsappAvailable;
    private String email;
    private String physicalAddress;

    private NextOfKinDTO nextOfKin;

    // Spiritual Journey & Church Membership
    private LocalDate dateJoinedChurch;
    private String status;
    private String baptismStatus;
    private LocalDate baptismDate;
    private String baptismLocation;
    private String baptismType;
    private String salvationStatus;
    private String churchExperienceRating;

    // Education & Occupation
    private String educationalLevel;
    private String occupation;
    private String employmentSector;
    private String employmentType;

    // Ministry Involvement & Skills
    private List<String> ministries;
    private String reasonForNonParticipation;
    private String leadershipRole;
    private List<String> skillsTalents;
    private List<String> spiritualGifts;

    // Welfare & Health Information
    private Boolean hasHealthIssues;
    private String specialNeedsOrMedicalConditions;

    // Administrative & System Data
    private String createdBy;
}
