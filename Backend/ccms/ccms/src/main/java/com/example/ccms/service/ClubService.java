package com.example.ccms.service;

import com.example.ccms.dto.request.ClubCreateRequest;
import com.example.ccms.dto.request.ClubUpdateRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.ClubVO;

import java.util.List;

public interface ClubService {
    ApiResponse<ClubVO> createClub(ClubCreateRequest request, Long creatorId);
    ApiResponse<ClubVO> updateClub(Long clubId, ClubUpdateRequest request, Long userId);
    ApiResponse<Void> deleteClub(Long clubId, Long userId);
    ApiResponse<List<ClubVO>> listClubs();
    ApiResponse<ClubVO> getClubById(Long clubId);
}