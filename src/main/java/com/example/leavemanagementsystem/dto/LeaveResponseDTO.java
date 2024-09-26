package com.example.leavemanagementsystem.dto;

import com.example.leavemanagementsystem.enums.LeaveType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.util.Date;

public record LeaveResponseDTO(
        @Enumerated(EnumType.STRING) LeaveType leaveType,
        LocalDate leaveStartDate,
        LocalDate leaveEndDate,
        Date resumptionDate
) {}

