package com.example.leavemanagementsystem.service;

import com.example.leavemanagementsystem.dto.CreateStaffRequestDTO;
import com.example.leavemanagementsystem.dto.LoginRequest;
import com.example.leavemanagementsystem.dto.ResponseDTO;
import com.example.leavemanagementsystem.dto.StaffResponseDTO;
import com.example.leavemanagementsystem.model.Role;
import com.example.leavemanagementsystem.model.Staff;

import java.util.Set;

public interface StaffService {
    StaffResponseDTO addStaff(CreateStaffRequestDTO staff);

    StaffResponseDTO editStaff(Long id, CreateStaffRequestDTO staffDetails);

    void deleteStaff(Long id);

    ResponseDTO<?> userLogin(LoginRequest loginRequest);

}
