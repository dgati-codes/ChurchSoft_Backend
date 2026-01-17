package com.churchsoft.users.service;

import com.churchsoft.global.dto.AccountType;
import com.churchsoft.global.dto.reponse.PageResult;
import com.churchsoft.global.exception.UserNotFoundException;
import com.churchsoft.members.entity.Member;
import com.churchsoft.members.repo.MemberRepository;
import com.churchsoft.users.constant.RoleName;
import com.churchsoft.users.constant.Status;
import com.churchsoft.users.dto.request.AuthRequest;
import com.churchsoft.users.dto.request.ChangePasswordRequest;
import com.churchsoft.users.dto.request.CreateUserRequest;
import com.churchsoft.users.dto.request.UpdateUserRequest;
import com.churchsoft.users.dto.response.AuthResponse;
import com.churchsoft.users.dto.response.UserResponse;
import com.churchsoft.users.entity.User;
import com.churchsoft.users.repo.ImageRepository;
import com.churchsoft.users.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;


    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            var jwtToken = jwtService.generateToken(user.getUsername());
            
            return AuthResponse.builder()
                    .token(jwtToken)
                    .username(user.getUsername())
                    .imageId(user.getImageId())
                    .firstname(user.getFirstName())
                    .lastname(user.getLastName())
                    .localAssemblyName(user.getLocalAssemblyName())
                    .status(user.getStatus())
                    .email(user.getEmail())
                    .roleName(user.getRoleName())
                    .profileImage(user.getProfileImage())
                    .build();
                    
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
    

    public UserResponse createUser(CreateUserRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .imageId(request.getImageId())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImage(request.getProfileImage())
                .phoneNumber(request.getPhoneNumber())
                .localAssemblyName(request.getLocalAssemblyName())
                .roleName(request.getRoleName() != null ? request.getRoleName() : RoleName.GUEST)
                .status(Status.ACTIVE)
                .build();
        
        var savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        
        return mapToUserResponse(savedUser);
    }
    

    public UserResponse getUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToUserResponse(user);
    }
    public PageResult<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdOn").descending());
        Page<User> userPage = userRepository.findAll(pageable);

        return PageResult.from(userPage, this::mapToUserResponse);
    }

    public UserResponse updateUser( UpdateUserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getId()));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setProfileImage(request.getProfileImage());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setLocalAssemblyName(request.getLocalAssemblyName());
        user.setStatus(request.getStatus());

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }


    public UserResponse getUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    public void deleteUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
        log.info("User deleted with ID: {}", id);
    }
    

    public UserResponse updateUserStatus(Long id, Status status) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setStatus(status);
        var updatedUser = userRepository.save(user);
        log.info("User status updated to {} for ID: {}", status, id);
        
        return mapToUserResponse(updatedUser);
    }


    public UserResponse getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        var username = authentication.getName();
        return getUserByUsername(username);
    }

    public void changePassword(ChangePasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public PageResult<UserResponse> searchUsersByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdOn").descending());
        Page<User> userPage = userRepository.searchByName(name, pageable);

        return PageResult.from(userPage, this::mapToUserResponse);
    }





    // Helper Method
    private UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .imageId(user.getImageId())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .phoneNumber(user.getPhoneNumber())
                .localAssemblyName(user.getLocalAssemblyName())
                .status(user.getStatus())
                .roleName(user.getRoleName())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }


    public void assignImage(AccountType accountType, Long entityId, Long imageId) {

        if (imageId != null && !imageRepository.existsById(imageId)) {
            throw new RuntimeException("Image not found");
        }

        switch (accountType) {
            case USER -> {
                User user = userRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                user.setImageId(imageId);
                userRepository.save(user);
            }
            case MEMBER -> {
                Member member = memberRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Member not found"));
                member.setImageId(imageId);
                memberRepository.save(member);
            }
        }
    }

}