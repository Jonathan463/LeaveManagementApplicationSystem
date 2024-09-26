package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.LeaveRequestDTO;
import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.model.LeaveRequest;
import com.example.leavemanagementsystem.service.impl.LeaveRequestServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Validated
public class LeaveController {
    @Autowired
    private LeaveRequestServiceImpl leaveRequestService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/leave")
    public ResponseEntity<LeaveRequest> createLeaveRequest(@Valid @RequestBody LeaveRequestDTO leaveRequest) throws Exception {
        String staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(leaveRequestService.createLeaveRequest(staffId, leaveRequest));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/leave/history")
    public ResponseEntity<List<LeaveResponseDTO>> getLeaveHistory() {
        String staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(leaveRequestService.getLeaveHistory(staffId));
    }
}
