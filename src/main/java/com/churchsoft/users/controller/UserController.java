package com.churchsoft.users.controller;


import com.churchsoft.global.dto.reponse.PageResult;
import com.churchsoft.users.constant.Status;
import com.churchsoft.users.dto.request.AuthRequest;
import com.churchsoft.users.dto.request.ChangePasswordRequest;
import com.churchsoft.users.dto.request.CreateUserRequest;
import com.churchsoft.users.dto.request.UpdateUserRequest;
import com.churchsoft.users.dto.response.AuthResponse;
import com.churchsoft.users.dto.response.UserResponse;
import com.churchsoft.users.service.JwtService;
import com.churchsoft.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/church-soft/v1.0/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private  final JwtService jwtService;

    @Operation(summary = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @Operation(summary = "Get a User by Id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get all Users")
    @GetMapping("/all")
    public ResponseEntity<PageResult<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<UserResponse> response = userService.getAllUsers(page, size);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Login user")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }
    @Operation(summary = "Get the current user")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
    @Operation(summary = "Update User")
    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }
    @Operation(summary = "Request for password change")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete User")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Update User Status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }
    @Operation(summary = "Search user by either firstname or lastname - case insensitive")
    @GetMapping("/search")
    public ResponseEntity<PageResult<UserResponse>> searchUsers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<UserResponse> response = userService.searchUsersByName(name, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh JWT token")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authHeader.substring(7); // remove "Bearer "

        try {
            String username = jwtService.extractUsername(token);
            UserResponse currentUser = userService.getUserByUsername(username);
            String newToken = jwtService.generateToken(username);

            AuthResponse response = AuthResponse.builder()
                    .token(newToken)
                    .username(currentUser.getUsername())
                    .firstname(currentUser.getFirstName())
                    .lastname(currentUser.getLastName())
                    .localAssemblyName(currentUser.getLocalAssemblyName())
                    .status(currentUser.getStatus())
                    .email(currentUser.getEmail())
                    .roleName(currentUser.getRoleName())
                    .profileImage(currentUser.getProfileImage())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @Operation(summary = "Get users by local assembly name")
    @GetMapping("/assembly/{assemblyName}")
    public ResponseEntity<List<UserResponse>> getUsersByAssembly(
            @PathVariable("assemblyName") String assemblyName) {
        return ResponseEntity.ok(userService.getUsersByAssembly(assemblyName));
    }


    @Operation(summary = "Get users by local assembly name")
    @GetMapping("/assembly/{assemblyName}/{status}")
    public ResponseEntity<List<UserResponse>> getUsersByAssemblyAndStatus(
            @PathVariable("assemblyName") String assemblyName,
            @PathVariable("status")  Status status) {
        return ResponseEntity.ok(userService.getUsersByAssemblyAndStatus(assemblyName, status));
    }
}