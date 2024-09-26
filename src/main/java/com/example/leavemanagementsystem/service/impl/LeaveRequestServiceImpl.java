package com.example.leavemanagementsystem.service.impl;

import com.example.leavemanagementsystem.dto.LeaveRequestDTO;
import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.exception.ResourceNotFoundException;
import com.example.leavemanagementsystem.model.LeaveRequest;
import com.example.leavemanagementsystem.model.Staff;
import com.example.leavemanagementsystem.repository.LeaveRequestRepository;
import com.example.leavemanagementsystem.repository.StaffRepository;
import com.example.leavemanagementsystem.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private StaffRepository staffRepository;

    private static final int SICK_LEAVE_BALANCE = 10;
    private static final int EXAM_LEAVE_BALANCE = 5;
    private static final int ANNUAL_LEAVE_BALANCE = 20;
    private static final int MAX_ANNUAL_LEAVE_AT_ONCE = 14;

    public LeaveRequest createLeaveRequest(String staffId, LeaveRequestDTO leaveDTO) throws Exception {
        Staff staff = staffRepository.findByStaffId(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        int leaveBalance = calculateLeaveBalance(staff, leaveDTO.leaveType().toString(), leaveDTO.leaveStartDate(), leaveDTO.leaveEndDate());

        if (leaveBalance < 0) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setDateRequested(LocalDate.now());
        leaveRequest.setInitialLeaveBalance(leaveBalance);
        leaveRequest.setLeaveType(leaveDTO.leaveType().toString());
        leaveRequest.setLeaveStartDate(leaveDTO.leaveStartDate());
        leaveRequest.setStatus(LeaveStatus.PENDING);




//        leaveRequest.InitialLeaveBalance(leaveBalance);
//        leaveRequest.setDateRequested(LocalDate.now());
//        leaveRequest.setStatus(LeaveStatus.PENDING);
//        leaveRequest.setManagerId(staff.getLineManagerId());

        return leaveRequestRepository.save(leaveRequest);
    }

    private int calculateLeaveBalance(Staff staff, String leaveType, LocalDate start, LocalDate end) throws Exception {
        // Calculate the number of requested leave days
        long leaveDays = ChronoUnit.DAYS.between(start, end);

        if (leaveDays <= 0) {
            throw new Exception("Invalid leave duration: End date must be after start date.");
        }

        int currentBalance;
        int maxLeaveAtOnce = Integer.MAX_VALUE;  // Default for leaves without restrictions on consecutive days

        switch (leaveType.toUpperCase()) {
            case "SICK_LEAVE":
                currentBalance = staff.getSickLeaveBalance();
                if (leaveDays > SICK_LEAVE_BALANCE) {
                    throw new Exception("Sick leave request exceeds available balance.");
                }
                break;

            case "EXAM_LEAVE":
                currentBalance = staff.getExamLeaveBalance();
                if (leaveDays > EXAM_LEAVE_BALANCE) {
                    throw new Exception("Exam leave request exceeds available balance.");
                }
                break;

            case "ANNUAL_LEAVE":
                currentBalance = staff.getAnnualLeaveBalance();
                maxLeaveAtOnce = MAX_ANNUAL_LEAVE_AT_ONCE;
                if (leaveDays > maxLeaveAtOnce) {
                    throw new Exception("Cannot take more than " + MAX_ANNUAL_LEAVE_AT_ONCE + " days of annual leave at a time.");
                }
                if (leaveDays > currentBalance) {
                    throw new Exception("Annual leave request exceeds available balance.");
                }
                break;

            case "COMPASSIONATE_LEAVE":
                currentBalance = staff.getCompassionateLeaveBalance();
                // Compassionate leave doesn't have a typical balance constraint, so we don't enforce strict balance rules here.
                // Implement logic based on company policy if needed.
                break;

            default:
                throw new Exception("Invalid leave type: " + leaveType);
        }

        // Deduct the leave days from the current balance
        int newBalance = (int) (currentBalance - leaveDays);

        // Return the new balance
        return newBalance;
    }


    public List<LeaveResponseDTO> getLeaveHistory(String staffId) {
        return leaveRequestRepository.findByStaffId(staffId);
    }

    public List<LeaveResponseDTO> getPendingRequestsForManager(String managerId) {
        return leaveRequestRepository.findByManagerIdAndStatus(managerId, LeaveStatus.PENDING);
    }

    public LeaveRequest approveLeave(Long requestId) throws Exception {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        request.setStatus(LeaveStatus.APPROVED);
        request.setDateApproved(LocalDate.now());

        Staff staff = staffRepository.findByStaffId(request.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        updateLeaveBalance(staff, request);

        return leaveRequestRepository.save(request);
    }

    private void updateLeaveBalance(Staff staff, LeaveRequest request) throws Exception {

            String leaveType = request.getLeaveType();
            LocalDate startDate = request.getLeaveStartDate();
            LocalDate endDate = request.getLeaveEndDate();

            // Calculate the number of leave days requested
            long leaveDays = ChronoUnit.DAYS.between(startDate, endDate);

            if (leaveDays <= 0) {
                throw new Exception("Invalid leave duration: End date must be after start date.");
            }

            // Update the leave balance based on the leave type
            switch (leaveType.toUpperCase()) {
                case "SICK_LEAVE":
                    if (staff.getSickLeaveBalance() < leaveDays) {
                        throw new Exception("Insufficient sick leave balance.");
                    }
                    staff.setSickLeaveBalance((int) (staff.getSickLeaveBalance() - leaveDays));
                    break;

                case "EXAM_LEAVE":
                    if (staff.getExamLeaveBalance() < leaveDays) {
                        throw new Exception("Insufficient exam leave balance.");
                    }
                    staff.setExamLeaveBalance((int) (staff.getExamLeaveBalance() - leaveDays));
                    break;

                case "ANNUAL_LEAVE":
                    if (leaveDays > 14) {
                        throw new Exception("Cannot request more than 14 days of annual leave at once.");
                    }
                    if (staff.getAnnualLeaveBalance() < leaveDays) {
                        throw new Exception("Insufficient annual leave balance.");
                    }
                    staff.setAnnualLeaveBalance((int) (staff.getAnnualLeaveBalance() - leaveDays));
                    break;

                case "COMPASSIONATE_LEAVE":
                    if (staff.getCompassionateLeaveBalance() < leaveDays) {
                        throw new Exception("Insufficient compassionate leave balance.");
                    }
                    staff.setCompassionateLeaveBalance((int) (staff.getCompassionateLeaveBalance() - leaveDays));
                    break;

                default:
                    throw new Exception("Invalid leave type: " + leaveType);
            }
        }
    @Transactional
    public void rejectLeave(Long requestId) throws Exception {
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestRepository.findById(requestId);

        if (!leaveRequestOptional.isPresent()) {
            throw new Exception("Leave request with ID " + requestId + " not found.");
        }

        LeaveRequest leaveRequest = leaveRequestOptional.get();

        // Check if the leave request is already approved or rejected
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new Exception("Cannot reject a leave request that is already " + leaveRequest.getStatus());
        }

        // Update the status of the leave request to REJECTED
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setDateApproved(LocalDate.now()); // Set the current date as the rejection date

        leaveRequestRepository.save(leaveRequest);
    }
}
