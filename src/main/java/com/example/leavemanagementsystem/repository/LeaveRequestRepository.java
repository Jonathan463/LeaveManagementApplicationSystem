package com.example.leavemanagementsystem.repository;

import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.model.LeaveRequest;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {

    List<LeaveResponseDTO> findByManagerIdAndStatus(String managerId, LeaveStatus leaveStatus);

    //List<LeaveRequest> findByStaffId(String staffId);
    @Query(value = "SELECT * FROM leave_request WHERE email = ?1", nativeQuery = true)
    List<LeaveRequest> findAllLeave(String email);
}
