package com.example.ccms.service;

import com.example.ccms.dto.request.ClubApplyRequest;
import com.example.ccms.dto.response.ApiResponse;

public interface ClubApplicationService {
    ApiResponse<Void> applyJoinClub(ClubApplyRequest request, Long userId);
    ApiResponse<Void> approveApplication(Long applicationId, Long clubId, Boolean isApproved, String feedback);
}