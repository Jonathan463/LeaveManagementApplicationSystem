package com.example.leavemanagementsystem.dto;

import com.example.leavemanagementsystem.enums.LeaveType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Date;

public record LeaveRequestDTO(
        @NotEmpty(message = "leave type cannot be null or empty")
        @Enumerated(EnumType.STRING) LeaveType leaveType,
        @NotEmpty(message = "leave start date cannot be null or empty")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "date must follow the pattern YYYY-MM-DD")
        LocalDate leaveStartDate,
        @NotEmpty(message = "leave end date cannot be null or empty")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "date must follow the pattern YYYY-MM-DD")
        LocalDate leaveEndDate,
        @NotEmpty(message = "Resumption date cannot be null or empty")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "date must follow the pattern YYYY-MM-DD")
        LocalDate resumptionDate
) {}

