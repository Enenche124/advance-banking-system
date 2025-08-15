package com.apostle.controllers;

import com.apostle.data.model.User;
import com.apostle.data.repositories.UserRepository;
import com.apostle.dtos.responses.UserProfileResponse;
import com.apostle.services.JwtService;
import com.apostle.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
@PreAuthorize("hasRole('CUSTOMER')")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @PostMapping("/{userId}/upload-photo")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable String  userId,
                                                       @RequestParam("file") MultipartFile file) {
        String imageUrl = userService.uploadProfilePicture(userId, file);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractAllClaims(token).getSubject();

            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserProfileResponse response = new UserProfileResponse(
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileImagePath()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid or expired token: " + e.getMessage());
        }
    }


}
