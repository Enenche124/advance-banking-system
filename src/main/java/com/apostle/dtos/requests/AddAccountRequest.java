package com.apostle.dtos.requests;

import lombok.Data;

@Data
public class AddAccountRequest {
    private String name;
    private String description;
}
