package com.churchsoft.members.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MemberCompletionDTO {
    private Long id;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String memberId;
    private Boolean isCompleted;
    private int completionPercentage;

    public MemberCompletionDTO(Long id, String fullName, String email,
                               LocalDateTime createdAt, LocalDateTime updatedAt,
                               String memberId, Boolean isCompleted,
                               int completionPercentage) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberId = memberId;
        this.isCompleted = isCompleted;
        this.completionPercentage = completionPercentage;
    }

}