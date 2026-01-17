package com.churchsoft.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Current password is required")
    private String newPassword;
    
    @NotBlank(message = "New password is required")
    @Size(message = "New password must be at least 6 characters")
    private String confirmPassword;
}