package com.example.leavemanagementsystem.service.impl;

import com.example.leavemanagementsystem.dto.LeaveRequestDTO;
import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.dto.ResponseDTO;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.exception.ResourceNotFoundException;
import com.example.leavemanagementsystem.model.LeaveRequest;
import com.example.leavemanagementsystem.model.Staff;
import com.example.leavemanagementsystem.repository.LeaveRequestRepository;
import com.example.leavemanagementsystem.repository.StaffRepository;
import com.example.leavemanagementsystem.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private StaffRepository staffRepository;

    private static final int SICK_LEAVE_BALANCE = 10;
    private static final int EXAM_LEAVE_BALANCE = 5;
    private static final int ANNUAL_LEAVE_BALANCE = 20;
    private static final int MAX_ANNUAL_LEAVE_AT_ONCE = 14;

    public LeaveRequest createLeaveRequest(String email, LeaveRequestDTO leaveDTO) throws Exception {

        Staff staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        int currentLeaveBal = calculateLeaveInitialBalance(staff, leaveDTO.leaveType().toString());

        int leaveNewBalance = calculateLeaveBalance(staff, leaveDTO.leaveType().toString(), leaveDTO.leaveStartDate(), leaveDTO.leaveEndDate());
        log.info("Calculate leave balance ****************** {}",leaveNewBalance);
        if (leaveNewBalance < 0) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setDateRequested(LocalDate.now());
        leaveRequest.setEmail(email);
        leaveRequest.setInitialLeaveBalance(currentLeaveBal);
        leaveRequest.setLeaveType(leaveDTO.leaveType().toString());
        leaveRequest.setLeaveStartDate(leaveDTO.leaveStartDate());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setResumptionDate(leaveDTO.leaveEndDate());
        leaveRequest.setLeaveEndDate(leaveDTO.leaveEndDate());
        leaveRequest.setManagerId(staff.getLineManagerId());
        leaveRequest.setFinalLeaveBalance(leaveNewBalance);

        log.info("About to save leave request **************************");
        return leaveRequestRepository.save(leaveRequest);
    }

    private int calculateLeaveBalance(Staff staff, String leaveType, LocalDate start, LocalDate end) throws Exception {
        // Calculate the number of requested leave days
        long leaveDays = ChronoUnit.DAYS.between(start, end);
        log.info("Leave Days **************** {}",leaveDays);

        if (leaveDays <= 0) {
            throw new Exception("Invalid leave duration: End date must be after start date.");
        }

        int currentBalance;
        int newBalance = 0;
        int maxLeaveAtOnce = Integer.MAX_VALUE;  // Default for leaves without restrictions on consecutive days

        switch (leaveType.toUpperCase()) {
            case "SICK":
                currentBalance = staff.getSickLeaveBalance();
                if (leaveDays > SICK_LEAVE_BALANCE && leaveDays > currentBalance) {
                    throw new Exception("Sick leave request exceeds available balance.");
                }
                newBalance =  (int)(currentBalance - leaveDays);
                break;

            case "EXAM":
                currentBalance = staff.getExamLeaveBalance();
                if (leaveDays > EXAM_LEAVE_BALANCE) {
                    throw new Exception("Exam leave request exceeds available balance.");
                }
                newBalance =  (int)(currentBalance - leaveDays);
                break;

            case "ANNUAL":
                currentBalance = staff.getAnnualLeaveBalance();
                maxLeaveAtOnce = MAX_ANNUAL_LEAVE_AT_ONCE;
                if (leaveDays > maxLeaveAtOnce) {
                    throw new Exception("Cannot take more than " + MAX_ANNUAL_LEAVE_AT_ONCE + " days of annual leave at a time.");
                }
                if (leaveDays > currentBalance) {
                    throw new Exception("Annual leave request exceeds available balance.");
                }
                break;

            case "COMPASSIONATE":
                currentBalance = staff.getCompassionateLeaveBalance();
                // Compassionate leave doesn't have a typical balance constraint, so we don't enforce strict balance rules here.
                break;

            default:
                throw new Exception("Invalid leave type: " + leaveType);
        }

        // Return the new balance
        return newBalance;
    }

    private int calculateLeaveInitialBalance(Staff staff, String leaveType) throws Exception {

        int currentBalance;
        switch (leaveType.toUpperCase()) {
            case "SICK":
                currentBalance = staff.getSickLeaveBalance();
                break;

            case "EXAM":
                currentBalance = staff.getExamLeaveBalance();;
                break;

            case "ANNUAL":
                currentBalance = staff.getAnnualLeaveBalance();
                break;

            case "COMPASSIONATE":
                currentBalance = staff.getCompassionateLeaveBalance();
                break;

            default:
                throw new Exception("Invalid leave type: " + leaveType);
        }

        // Return the new balance
        return currentBalance;
    }


    public List<LeaveRequest> getLeaveHistory(String email) {
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAllLeave(email);
        log.info("Leave history from DB {}",leaveRequestList);
        return leaveRequestList;
    }

    @Override
    public ResponseDTO<?> getPendingRequestsForManager(String email) {
        Staff staff = staffRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Manager Not Found"));
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findByManagerIdAndStatus(staff.getId(), LeaveStatus.PENDING.name());

        return ResponseDTO.builder()
                .statusCode(200)
                .responseMessage(leaveRequestList.isEmpty() ? "Data is Empty" : "Successfully Fetch Leave Request Data")
                .data(leaveRequestList)
                .build();
    }
    @Override
    public ResponseDTO<?> getRequestsForManager(String email) {
        Staff staff = staffRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Manager Not Found"));
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findByManagerId(staff.getId());
        return ResponseDTO.builder()
                .statusCode(200)
                .responseMessage(leaveRequestList.isEmpty() ? "Data is Empty" : "Successfully Fetch Leave Request Data")
                .data(leaveRequestList)
                .build();
    }

    @Override
    public LeaveRequest approveLeave2(String email) throws Exception {
        LeaveRequest request =  leaveRequestRepository.findByUserEmailAndStatus(email, LeaveStatus.PENDING).orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        request.setStatus(LeaveStatus.APPROVED);
        request.setDateApproved(LocalDate.now());

        Staff staff = staffRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        updateLeaveBalance(staff, request);
        staffRepository.save(staff);

        return leaveRequestRepository.save(request);
    }

    public LeaveRequest approveLeave(Long requestId) throws Exception {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        request.setStatus(LeaveStatus.APPROVED);
        request.setDateApproved(LocalDate.now());

        Staff staff = staffRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        updateLeaveBalance(staff, request);
        staffRepository.save(staff);

        return leaveRequestRepository.save(request);
    }

    private void updateLeaveBalance(Staff staff, LeaveRequest request) throws Exception {

        String leaveType = request.getLeaveType();
        LocalDate startDate = request.getLeaveStartDate();
        LocalDate endDate = request.getLeaveEndDate();

        // Calculate the number of leave days requested
        long leaveDays = ChronoUnit.DAYS.between(startDate, endDate);

        // Update the leave balance based on the leave type
        switch (leaveType.toUpperCase()) {
            case "SICK":
                if (staff.getSickLeaveBalance() < leaveDays) {
                    throw new Exception("Insufficient sick leave balance.");
                }
                staff.setSickLeaveBalance((int) (staff.getSickLeaveBalance() - leaveDays));
                break;

            case "EXAM":
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
