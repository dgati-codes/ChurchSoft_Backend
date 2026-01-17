package com.churchsoft.users.dto.response;

import com.churchsoft.users.constant.RoleName;
import com.churchsoft.users.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private Long imageId;
    private String lastName;
    private String username;
    private String email;
    private String profileImage;
    private String phoneNumber;
    private String localAssemblyName;
    private Status status;
    private RoleName roleName;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}