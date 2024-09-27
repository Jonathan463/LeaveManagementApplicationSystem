package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.CreateStaffRequestDTO;
import com.example.leavemanagementsystem.dto.LoginRequest;
import com.example.leavemanagementsystem.dto.ResponseDTO;
import com.example.leavemanagementsystem.model.Role;
import com.example.leavemanagementsystem.enums.RoleName;
import com.example.leavemanagementsystem.security.JwtAuthenticationHelper;
import com.example.leavemanagementsystem.service.StaffService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StaffService userService;


    @Autowired
    private JwtAuthenticationHelper jwtTokenProvider;  // JWT token generator

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody CreateStaffRequestDTO createStaffRequestDTO) {

        userService.addStaff(createStaffRequestDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

       ResponseDTO responseDTO = userService.userLogin(loginRequest);

       int status = responseDTO.getStatusCode();

        return new ResponseEntity<>(responseDTO, status != 200 ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }
}
