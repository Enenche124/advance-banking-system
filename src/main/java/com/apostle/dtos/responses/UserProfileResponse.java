package com.apostle.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record UserProfileResponse(String id, String username, String email, String profileImagePath) {}
