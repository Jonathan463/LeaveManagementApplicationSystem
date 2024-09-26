package com.example.leavemanagementsystem.dto;

import com.example.leavemanagementsystem.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record StaffResponseDTO(
        String firstName,
        String lastName,
        String staffId,
        String role
) {}

