package com.example.ccms.controller;

import com.example.ccms.dto.request.ClubCreateRequest;
import com.example.ccms.dto.request.ClubUpdateRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.ClubVO;
import com.example.ccms.security.CurrentUser;
import com.example.ccms.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * 创建社团
     * 权限：仅ADMIN或MANAGER可访问
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // 方法级权限控制
    public ApiResponse<ClubVO> createClub(
            @Valid @RequestBody ClubCreateRequest request,
            @CurrentUser Long userId // 当前登录用户ID
    ) {
        return clubService.createClub(request, userId);
    }

    /**
     * 更新社团信息
     * 权限：仅ADMIN或MANAGER可访问，且必须是社团创建人
     */
    @PutMapping("/{clubId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // 方法级权限控制
    public ApiResponse<ClubVO> updateClub(
            @PathVariable Long clubId,
            @Valid @RequestBody ClubUpdateRequest request,
            @CurrentUser Long userId
    ) {
        return clubService.updateClub(clubId, request, userId);
    }

    /**
     * 查询社团列表（开放招新的）
     * 权限：登录用户均可访问
     */
    @GetMapping
    public ApiResponse<List<ClubVO>> listClubs() {
        return clubService.listClubs();
    }

    /**
     * 删除社团
     * 权限：仅ADMIN或MANAGER可访问，且必须是社团创建人
     */
    @DeleteMapping("/{clubId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // 方法级权限控制
    public ApiResponse<Void> deleteClub(
            @PathVariable Long clubId,
            @CurrentUser Long userId
    ) {
        return clubService.deleteClub(clubId, userId);
    }
}