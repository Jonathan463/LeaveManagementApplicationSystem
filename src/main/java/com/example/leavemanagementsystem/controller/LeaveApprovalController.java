package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.dto.ResponseDTO;
import com.example.leavemanagementsystem.model.LeaveRequest;
import com.example.leavemanagementsystem.security.JwtAuthenticationHelper;
import com.example.leavemanagementsystem.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manager")
@Validated
public class LeaveApprovalController {
    @Autowired
    private LeaveRequestService leaveRequestService;
    @Autowired
    private JwtAuthenticationHelper jwtHelper;

    @GetMapping("/leaves/pending")
    public ResponseEntity<ResponseDTO<?>> getPendingRequests(HttpServletRequest request) {
        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = requestHeader.substring(7);
        String email = jwtHelper.getUsernameFromToken(token);
        return ResponseEntity.ok(leaveRequestService.getPendingRequestsForManager(email));
    }

    @GetMapping("/leaves")
    public ResponseEntity<ResponseDTO<?>> getLeaveRequests(HttpServletRequest request) {
        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = requestHeader.substring(7);
        String email = jwtHelper.getUsernameFromToken(token);
        return ResponseEntity.ok(leaveRequestService.getRequestsForManager(email));
    }

    @PostMapping("/leaves/approve")
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable @Pattern(regexp = "^\\d+$") Long requestId) throws Exception {
        return ResponseEntity.ok(leaveRequestService.approveLeave(requestId));
    }
    @PostMapping("/leaves/approve2")
    public ResponseEntity<LeaveRequest> approveLeave2(@RequestParam String email) throws Exception {
        return ResponseEntity.ok(leaveRequestService.approveLeave2(email));
    }

    @PostMapping("/leaves/reject/{requestId}")
    public ResponseEntity<Void> rejectLeave(@PathVariable
                                            @Pattern(regexp = "^\\d+$")
                                            Long requestId) throws Exception {
        leaveRequestService.rejectLeave(requestId);
        return ResponseEntity.ok().build();
    }
}
