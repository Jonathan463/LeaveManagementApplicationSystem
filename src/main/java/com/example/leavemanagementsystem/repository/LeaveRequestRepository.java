package com.example.leavemanagementsystem.repository;

import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.model.LeaveRequest;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {

    List<LeaveRequest> findByManagerIdAndStatus(long managerId, String leaveStatus);

    //List<LeaveRequest> findByStaffId(String staffId);
    @Query(value = "SELECT * FROM leave_request WHERE email = ?1", nativeQuery = true)
    List<LeaveRequest> findAllLeave(String email);

    @Query("SELECT u FROM LeaveRequest u WHERE u.managerId = :id")
    List<LeaveRequest> findByManagerId(Long id);

    @Query("SELECT u FROM LeaveRequest u WHERE u.email = :email AND u.status = :status")
    Optional<LeaveRequest> findByUserEmailAndStatus(String email, LeaveStatus status);
}
