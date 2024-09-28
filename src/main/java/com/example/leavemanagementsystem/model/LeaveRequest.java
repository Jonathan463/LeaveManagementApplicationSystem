package com.example.leavemanagementsystem.model;

import com.example.leavemanagementsystem.enums.LeaveStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String leaveType;
    private LocalDate leaveStartDate;
    private LocalDate leaveEndDate;
    private LocalDate resumptionDate;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;

    private Integer initialLeaveBalance;
    private Integer finalLeaveBalance;

    private LocalDate dateRequested;
    private LocalDate dateApproved;

    // Assuming reference to staff's line manager for approvals
    private long managerId;
}
