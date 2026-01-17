package com.churchsoft.users.dto.request;

import com.churchsoft.users.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String profileImage;
    private String phoneNumber;
    private String localAssemblyName;
    private Status status;
}