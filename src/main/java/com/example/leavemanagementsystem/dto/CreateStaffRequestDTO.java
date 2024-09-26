package com.example.leavemanagementsystem.dto;

import com.example.leavemanagementsystem.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateStaffRequestDTO(
        @NotEmpty(message = "first name cannot be null or empty")
        @Size(min = 3, max = 30, message = "The name of the staff should be between 3 and 30")
        String firstName,
        @NotEmpty(message = "last name cannot be null or empty")
        @Size(min = 3, max = 30, message = "The name of the staff should be between 3 and 30")
        String lastName,
        @Email(message = "must be a valid email")
        String email,
        @NotEmpty(message = "department cannot be null or empty")
        String department,
        @NotEmpty(message = "password cannot be null or empty")
        String password,
        String role

) {}

