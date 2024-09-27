package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.LeaveRequestDTO;
import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.model.LeaveRequest;
import com.example.leavemanagementsystem.security.JwtAuthenticationHelper;
import com.example.leavemanagementsystem.service.impl.LeaveRequestServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
@Slf4j
public class LeaveController {
    @Autowired
    private LeaveRequestServiceImpl leaveRequestService;

    @Autowired
    private  JwtAuthenticationHelper jwtHelper;

    @PostMapping("/leave")
    public ResponseEntity<LeaveRequest> createLeaveRequest(@Valid @RequestBody LeaveRequestDTO leaveRequest, HttpServletRequest request) throws Exception {

        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = requestHeader.substring(7);
        String email = jwtHelper.getUsernameFromToken(token);
        log.info("Got here {}", email);
        return ResponseEntity.ok(leaveRequestService.createLeaveRequest(email, leaveRequest));
    }

    @GetMapping("/leave/history")
    public ResponseEntity<List<LeaveRequest>> getLeaveHistory(HttpServletRequest request) {
        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = requestHeader.substring(7);
        String email = jwtHelper.getUsernameFromToken(token);
        log.info("requesting leave history for::::::::::::::::::::::  {}",email);
        return ResponseEntity.ok(leaveRequestService.getLeaveHistory(email));
    }
}
