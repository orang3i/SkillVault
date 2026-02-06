package com.orang3i.skillvault.dto;

import jakarta.validation.constraints.*;

public class NodeUpdateRequest {

    @NotBlank(message = "Title is required and cannot be blank")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    public String title;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    public String description;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    public String category;

    @Min(value = 0, message = "Mastery must be at least 0")
    @Max(value = 100, message = "Mastery cannot exceed 100")
    public int mastery;
}