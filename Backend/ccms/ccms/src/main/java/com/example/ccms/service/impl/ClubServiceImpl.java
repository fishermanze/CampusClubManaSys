package com.example.ccms.service.impl;

import com.example.ccms.dto.request.ClubCreateRequest;
import com.example.ccms.dto.request.ClubUpdateRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.ClubVO;
import com.example.ccms.entity.Club;
import com.example.ccms.entity.ClubImage;
import com.example.ccms.enums.ErrorCodeEnum;
import com.example.ccms.enums.RecruitStatusEnum;
import com.example.ccms.exception.BusinessException;
import com.example.ccms.repository.ClubImageRepository;
import com.example.ccms.repository.ClubRepository;
import com.example.ccms.service.ClubService;
import com.example.ccms.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final ClubImageRepository clubImageRepository;

    @Override
    @Transactional
    public ApiResponse<ClubVO> createClub(ClubCreateRequest request, Long creatorId) {
        if (clubRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCodeEnum.CLUB_NAME_EXIST);
        }

        Club club = new Club();
        club.setName(request.getName());
        club.setDirection(request.getDirection());
        club.setIntroduction(request.getIntroduction());
        club.setMaxNumber(request.getMaxNumber());
        club.setRecruitStatus(request.getRecruitStatus() == null ? RecruitStatusEnum.OPEN : request.getRecruitStatus());
        club.setCreatorId(creatorId);
        Club savedClub = clubRepository.save(club);

        ClubVO clubVO = new ClubVO();
        clubVO.setId(savedClub.getId());
        clubVO.setName(savedClub.getName());
        clubVO.setDirection(savedClub.getDirection());
        clubVO.setIntroduction(savedClub.getIntroduction());
        clubVO.setMaxNumber(savedClub.getMaxNumber());
        clubVO.setRecruitStatus(savedClub.getRecruitStatus());

        if (StringUtils.hasText(request.getImageUrl())) {
            ClubImage mainImage = new ClubImage();
            mainImage.setClubId(savedClub.getId());
            mainImage.setImageUrl(request.getImageUrl());
            mainImage.setIsMain(true);
            mainImage.setSort(1);
            clubImageRepository.save(mainImage);
            clubVO.setMainImageUrl(request.getImageUrl());
        }

        return ResultUtil.success(clubVO);
    }

    @Override
    @Transactional
    public ApiResponse<ClubVO> updateClub(Long clubId, ClubUpdateRequest request, Long userId) {
        Club club = clubRepository.findByIdAndCreatorId(clubId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.CLUB_NOT_FOUND));

        if (StringUtils.hasText(request.getName())) {
            club.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDirection())) {
            club.setDirection(request.getDirection());
        }
        if (StringUtils.hasText(request.getIntroduction())) {
            club.setIntroduction(request.getIntroduction());
        }
        if (request.getMaxNumber() != null) {
            club.setMaxNumber(request.getMaxNumber());
        }
        if (request.getRecruitStatus() != null) {
            club.setRecruitStatus(request.getRecruitStatus());
        }

        Club updatedClub = clubRepository.save(club);

        ClubVO clubVO = new ClubVO();
        clubVO.setId(updatedClub.getId());
        clubVO.setName(updatedClub.getName());
        clubVO.setDirection(updatedClub.getDirection());
        clubVO.setIntroduction(updatedClub.getIntroduction());
        clubVO.setMaxNumber(updatedClub.getMaxNumber());
        clubVO.setRecruitStatus(updatedClub.getRecruitStatus());

        clubImageRepository.findFirstByClubIdAndIsMainTrue(updatedClub.getId())
                .ifPresent(image -> clubVO.setMainImageUrl(image.getImageUrl()));

        return ResultUtil.success(clubVO);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteClub(Long clubId, Long userId) {
        Club club = clubRepository.findByIdAndCreatorId(clubId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.CLUB_NOT_FOUND));

        clubImageRepository.deleteByClubId(clubId);
        clubRepository.delete(club);

        return ResultUtil.success();
    }

    @Override
    public ApiResponse<List<ClubVO>> listClubs() {
        List<Club> clubs = clubRepository.findByRecruitStatus(RecruitStatusEnum.OPEN);
        List<ClubVO> clubVOs = clubs.stream().map(club -> {
            ClubVO vo = new ClubVO();
            vo.setId(club.getId());
            vo.setName(club.getName());
            vo.setDirection(club.getDirection());
            vo.setIntroduction(club.getIntroduction());
            vo.setMaxNumber(club.getMaxNumber());
            vo.setRecruitStatus(club.getRecruitStatus());

            clubImageRepository.findFirstByClubIdAndIsMainTrue(club.getId())
                    .ifPresent(image -> vo.setMainImageUrl(image.getImageUrl()));

            return vo;
        }).collect(Collectors.toList());
        return ResultUtil.success(clubVOs);
    }

    @Override
    public ApiResponse<ClubVO> getClubById(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.CLUB_NOT_FOUND));

        ClubVO clubVO = new ClubVO();
        clubVO.setId(club.getId());
        clubVO.setName(club.getName());
        clubVO.setDirection(club.getDirection());
        clubVO.setIntroduction(club.getIntroduction());
        clubVO.setMaxNumber(club.getMaxNumber());
        clubVO.setRecruitStatus(club.getRecruitStatus());

        clubImageRepository.findFirstByClubIdAndIsMainTrue(club.getId())
                .ifPresent(image -> clubVO.setMainImageUrl(image.getImageUrl()));

        return ResultUtil.success(clubVO);
    }
}