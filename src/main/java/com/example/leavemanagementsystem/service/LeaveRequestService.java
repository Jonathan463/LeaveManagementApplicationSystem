package com.example.leavemanagementsystem.service;

import com.example.leavemanagementsystem.dto.LeaveRequestDTO;
import com.example.leavemanagementsystem.dto.LeaveResponseDTO;
import com.example.leavemanagementsystem.model.LeaveRequest;

import java.util.List;

public interface LeaveRequestService {
    public LeaveRequest createLeaveRequest(String staffId, LeaveRequestDTO leaveDTO) throws Exception;
    public List<LeaveRequest> getLeaveHistory(String staffId);
    public List<LeaveResponseDTO> getPendingRequestsForManager(String managerId);
    public LeaveRequest approveLeave(Long requestId) throws Exception;
    public void rejectLeave(Long requestId) throws Exception;
}
