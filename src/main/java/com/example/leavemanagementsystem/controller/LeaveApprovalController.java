package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.model.LeaveRequest;
import com.example.leavemanagementsystem.service.LeaveRequestService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager")
@Validated
public class LeaveApprovalController {
    @Autowired
    private LeaveRequestService leaveRequestService;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/leave/pending")
    public ResponseEntity<List<LeaveResponseDTO>> getPendingRequests() {
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(leaveRequestService.getPendingRequestsForManager(managerId));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/leave/approve/{requestId}")
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable
                                                         @Pattern(regexp = "^\\d+$")
                                                         Long requestId) throws Exception {
        return ResponseEntity.ok(leaveRequestService.approveLeave(requestId));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/leave/reject/{requestId}")
    public ResponseEntity<Void> rejectLeave(@PathVariable
                                                @Pattern(regexp = "^\\d+$")
                                                Long requestId) throws Exception {
        leaveRequestService.rejectLeave(requestId);
        return ResponseEntity.ok().build();
    }
}
