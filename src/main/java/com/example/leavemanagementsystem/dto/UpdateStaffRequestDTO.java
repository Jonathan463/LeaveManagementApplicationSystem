package com.example.leavemanagementsystem.dto;

import com.example.leavemanagementsystem.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UpdateStaffRequestDTO(
        @NotEmpty(message = "first name cannot be null or empty")
        @Size(min = 3, max = 30, message = "The name of the staff should be between 3 and 30")
        String firstName,

        @NotEmpty(message = "last name cannot be null or empty")
        @Size(min = 3, max = 30, message = "The name of the staff should be between 3 and 30")
        String lastName,

        @NotEmpty(message = "staff id cannot be null or empty")
        String staffId,

        @NotEmpty(message = "role cannot be null or empty")
        @Enumerated(EnumType.STRING)
        Role role
) {}

