package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.CreateStaffRequestDTO;
import com.example.leavemanagementsystem.dto.StaffResponseDTO;
import com.example.leavemanagementsystem.model.Staff;
import com.example.leavemanagementsystem.service.StaffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
public class AdminController {
    @Autowired
    private StaffService staffService;


    @PostMapping("/staff")
    public ResponseEntity<StaffResponseDTO> addStaff(@Valid @RequestBody CreateStaffRequestDTO staff) {
        return ResponseEntity.ok(staffService.addStaff(staff));
    } // fine

    @PutMapping("/staff/{id}")
    public ResponseEntity<StaffResponseDTO> editStaff(@PathVariable
                                                      @Pattern(regexp = "^\\d+$")
                                                      Long id, @Valid @RequestBody CreateStaffRequestDTO staffDetails) {
        return ResponseEntity.ok(staffService.editStaff(id, staffDetails));
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable
                                         @Pattern(regexp = "^\\d+$")
                                         Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok().build();
    }
}
