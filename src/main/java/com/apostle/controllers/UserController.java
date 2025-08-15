package com.apostle.controllers;

import com.apostle.data.repositories.UserRepository;
import com.apostle.exceptions.UserNotFoundException;
import com.apostle.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
@PreAuthorize("hasRole('CUSTOMER')")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;


    @PutMapping(value = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfilePicture(
                                                       @RequestParam("file") MultipartFile file,
                                                       Authentication authentication
                                                       ) {

        String userEmail = authentication.getName();
        String userId = userRepository.findUserByEmail(userEmail)
                .orElseThrow(()-> new UserNotFoundException("User not found"))
                .getId();
        String imageUrl = userService.uploadProfilePicture(userId, file);
        return ResponseEntity.ok(imageUrl);
    }


}
