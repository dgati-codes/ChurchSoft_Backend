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
public class AuthResponse {
    private String token;
    private String username;
    private Long imageId;
    private String firstname;
    private String lastname;
    private String localAssemblyName;
    private Status status;
    private String email;
    private RoleName roleName;
    private String profileImage;
}