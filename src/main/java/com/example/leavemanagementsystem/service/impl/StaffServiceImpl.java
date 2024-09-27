package com.example.leavemanagementsystem.service.impl;

import com.example.leavemanagementsystem.dto.CreateStaffRequestDTO;
import com.example.leavemanagementsystem.dto.LoginRequest;
import com.example.leavemanagementsystem.dto.LoginResponseDTO;
import com.example.leavemanagementsystem.dto.ResponseDTO;
import com.example.leavemanagementsystem.dto.StaffResponseDTO;
import com.example.leavemanagementsystem.exception.ResourceNotFoundException;
import com.example.leavemanagementsystem.model.Role;
import com.example.leavemanagementsystem.model.Staff;
import com.example.leavemanagementsystem.repository.RoleRepository;
import com.example.leavemanagementsystem.repository.StaffRepository;
import com.example.leavemanagementsystem.security.JwtAuthenticationHelper;
import com.example.leavemanagementsystem.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class StaffServiceImpl implements StaffService, UserDetailsService {
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationHelper jwtHelper;
    private static final int SICK_LEAVE_BALANCE = 10;
    private static final int EXAM_LEAVE_BALANCE = 5;
    private static final int ANNUAL_LEAVE_BALANCE = 20;
    private static final int MAX_ANNUAL_LEAVE_AT_ONCE = 14;
    public StaffResponseDTO addStaff(CreateStaffRequestDTO createStaffRequestDTO) {

        if (staffRepository.findByEmail(createStaffRequestDTO.email()).isPresent()) {
            throw new RuntimeException("Staff already exists");
        }
        Role role = roleRepository.findByRoleName(createStaffRequestDTO.role()).orElseThrow(()-> new ResourceNotFoundException("Role does not exist"));
        Staff staff = new Staff();


        String stringRole = createStaffRequestDTO.role();
        if(stringRole.equalsIgnoreCase(("ADMIN"))){
            staff.setLineManagerId(0L);}
        else if(stringRole.equalsIgnoreCase(("MANAGER"))){
            staff.setLineManagerId(1L);
        }
        else {
            List<Staff> staffManagers = staffRepository.findByDepartmentAndLineManagerId(createStaffRequestDTO.department(), 1L);
            System.out.println(staffManagers.get(0));

            staff.setLineManagerId(staffManagers.get(0).getId());

        }

        //Role role = new Role();
        //role.setRoleName(stringRole);
        
        String staffId = UUID.randomUUID().toString();


        staff.setRole(role);
        staff.setStaffId(staffId);
        staff.setDepartment(createStaffRequestDTO.department());
        staff.setPassword(passwordEncoder.encode(createStaffRequestDTO.password()));
        staff.setFirstName(createStaffRequestDTO.firstName());
        staff.setLastName(createStaffRequestDTO.lastName());
        staff.setEmail(createStaffRequestDTO.email());
        staff.setAnnualLeaveBalance(ANNUAL_LEAVE_BALANCE);
        staff.setExamLeaveBalance(EXAM_LEAVE_BALANCE);
        staff.setSickLeaveBalance(SICK_LEAVE_BALANCE);
        staff.setCompassionateLeaveBalance(MAX_ANNUAL_LEAVE_AT_ONCE);

        Staff savedStaff = staffRepository.save(staff);



        return new StaffResponseDTO(savedStaff.getFirstName(), staff.getLastName(), savedStaff.getStaffId() ,savedStaff.getRole().getRoleName());

    }

    public StaffResponseDTO editStaff(Long id, CreateStaffRequestDTO staffDetails) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        

        Role role = new Role();
        role.setRoleName(staffDetails.role());
        
        Staff updatedStaff = new Staff();
        updatedStaff.setFirstName(staffDetails.firstName());
        updatedStaff.setLastName(staffDetails.lastName());
        updatedStaff.setRole(role);
        

                Staff savedStaff = staffRepository.save(updatedStaff);

        //StaffResponseDTO staffResponseDTO = new StaffResponseDTO(staff.getFirstName(),staff.getLastName(), staff.getStaffId(), savedStaff.getRole());

        //return staffResponseDTO;
        return null;
    }

    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        staffRepository.delete(staff);
    }



    @Override
    public ResponseDTO<?> userLogin(LoginRequest loginRequest) {

        Staff appUser;
        String token;
        if(loginRequest.getUserName().equalsIgnoreCase("Admin")){
            appUser = staffRepository.findByStaffId(loginRequest.getUserName()).orElseThrow(()->new ResourceNotFoundException("User does not exist"));
            token = jwtHelper.generateToken(appUser,appUser.getUsername());
        }
        else {
            appUser = loadUserByUsername(loginRequest.getUserName());
            token = jwtHelper.generateToken(appUser,appUser.getEmail());

        }

        if (!appUser.isEnabled()){

            return ResponseDTO.builder()
                    .statusCode(400)
                    .responseMessage("User Account deactivated")
                    .build();
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), appUser.getPassword())){


            return ResponseDTO.builder()
                    .statusCode(400)
                    .responseMessage("Invalid user credential")
                    .build();
        }

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .token(token)
                .build();
        return ResponseDTO.builder()
                .statusCode(200)
                .responseMessage("Login Successful")
                .data(loginResponseDTO)
                .build();
    }

    @Override
    public Staff loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.equalsIgnoreCase("ADMIN")){
            return staffRepository.findByStaffId(username).orElseThrow( () -> new UsernameNotFoundException("Username: " + username + " not found"));
        }
        return staffRepository.findByEmail(username).orElseThrow( () -> new UsernameNotFoundException("Username: " + username + " not found"));

    }
}
