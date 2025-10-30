package com.example.ccms.controller;

import com.example.ccms.dto.request.ClubApplyRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.security.CurrentUser;
import com.example.ccms.service.ClubApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ClubApplicationController {

    private final ClubApplicationService applicationService;

    @PostMapping
    public ApiResponse<Void> applyJoinClub(@Valid @RequestBody ClubApplyRequest request,
                                           @CurrentUser Long userId) {
        return applicationService.applyJoinClub(request, userId);
    }

    @PutMapping("/{applicationId}/approve")
    public ApiResponse<Void> approveApplication(@PathVariable Long applicationId,
                                                @RequestParam Long clubId,
                                                @RequestParam Boolean isApproved,
                                                @RequestParam(required = false) String feedback) {
        return applicationService.approveApplication(applicationId, clubId, isApproved, feedback);
    }
}