package com.example.leavemanagementsystem.service.impl;

import com.example.leavemanagementsystem.dto.CreateStaffRequestDTO;
import com.example.leavemanagementsystem.dto.LoginRequest;
import com.example.leavemanagementsystem.dto.LoginResponseDTO;
import com.example.leavemanagementsystem.dto.ResponseDTO;
import com.example.leavemanagementsystem.dto.StaffResponseDTO;
import com.example.leavemanagementsystem.exception.ResourceNotFoundException;
import com.example.leavemanagementsystem.model.Role;
import com.example.leavemanagementsystem.model.Staff;
import com.example.leavemanagementsystem.repository.StaffRepository;
import com.example.leavemanagementsystem.security.JwtAuthenticationHelper;
import com.example.leavemanagementsystem.service.StaffService;
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
public class StaffServiceImpl implements StaffService, UserDetailsService {
    @Autowired
    private StaffRepository staffRepository;

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
        Staff staff = new Staff();


        String stringRole = createStaffRequestDTO.role();
        if(stringRole.equalsIgnoreCase(("ADMIN"))){
            staff.setLineManagerId(0L);}
        else if(stringRole.equalsIgnoreCase(("MANAGER"))){
            staff.setLineManagerId(1L);
        }
        else {
            List<Staff> staffMamagers = staffRepository.findByDepartmentAndLineManagerId(createStaffRequestDTO.department(), 1L);
            System.out.println(staffMamagers.get(0));

            staff.setLineManagerId(staffMamagers.get(0).getId());

        }

        Role role = new Role();
        role.setRoleName(stringRole);
        
        String staffId = UUID.randomUUID().toString();


        staff.setRole(role);
        staff.setStaffId(staffId);
        staff.setDepartment(createStaffRequestDTO.department());
        staff.setPassword(passwordEncoder.encode(createStaffRequestDTO.password()));
        staff.setFirstName(createStaffRequestDTO.firstName());
        staff.setLastName(createStaffRequestDTO.lastName());
        staff.setAnnualLeaveBalance(ANNUAL_LEAVE_BALANCE);
        staff.setExamLeaveBalance(EXAM_LEAVE_BALANCE);
        staff.setSickLeaveBalance(SICK_LEAVE_BALANCE);
        staff.setCompassionateLeaveBalance(MAX_ANNUAL_LEAVE_AT_ONCE);

        Staff savedStaff = staffRepository.save(staff);


//        INSERT INTO public.staff(
//                annual_leave_balance, compassionate_leave_balance, exam_leave_balance, sick_leave_balance, line_manager_id, role_id, department, email, first_name, last_name, password, staff_id)
//        VALUES (20, 14, 5, 10, 0, 1, 'Maths', 'jonathan39@gmail.com', 'Jonathan', 'godson', '$2a$12$o9KFPRbCrSR.yBGPHP5D5.b9uu3rfIzCOF4XBRUWmcyFMiBb7CE3K', 'admin');
        return new StaffResponseDTO(savedStaff.getStaffId(), staff.getLastName(), savedStaff.getFirstName(),savedStaff.getRole().toString());

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
        if(loginRequest.getUserName().equalsIgnoreCase("Admin")){
            appUser = staffRepository.findByStaffId(loginRequest.getUserName()).orElseThrow(()->new ResourceNotFoundException("User does not exist"));
        }
        else {
            appUser = loadUserByUsername(loginRequest.getUserName());
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
        String token = jwtHelper.generateToken(appUser.getUsername());
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
        return staffRepository.findByEmail(username).orElseThrow( () -> new UsernameNotFoundException("Username: " + username + " not found"));
    }
}
