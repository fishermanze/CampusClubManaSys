package com.example.ccms.service.impl;

import com.example.ccms.dto.request.ClubApplyRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.entity.Club;
import com.example.ccms.entity.ClubApplication;
import com.example.ccms.entity.Users;
import com.example.ccms.enums.ApplicationStatusEnum;
import com.example.ccms.enums.ErrorCodeEnum;
import com.example.ccms.enums.MessageTypeEnum;
import com.example.ccms.enums.RecruitStatusEnum;
import com.example.ccms.exception.BusinessException;
import com.example.ccms.repository.ClubApplicationRepository;
import com.example.ccms.repository.ClubRepository;
import com.example.ccms.repository.UsersRepository;
import com.example.ccms.service.ClubApplicationService;
import com.example.ccms.service.MessageService;
import com.example.ccms.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClubApplicationServiceImpl implements ClubApplicationService {

    private final ClubApplicationRepository applicationRepository;
    private final ClubRepository clubRepository;
    private final UsersRepository usersRepository;
    private final MessageService messageService; // 新增：消息服务依赖

    @Override
    @Transactional
    public ApiResponse<Void> applyJoinClub(ClubApplyRequest request, Long userId) {
        // 校验社团是否存在
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.CLUB_NOT_FOUND));

        // 校验社团是否开放招新
        if (club.getRecruitStatus() == RecruitStatusEnum.CLOSED) {
            throw new BusinessException(ErrorCodeEnum.CLUB_RECRUIT_CLOSED);
        }

        // 校验是否重复申请
        if (applicationRepository.existsByUserIdAndClubId(userId, request.getClubId())) {
            throw new BusinessException(ErrorCodeEnum.APPLICATION_REPEAT);
        }

        // 保存申请记录
        ClubApplication application = new ClubApplication();
        application.setUserId(userId);
        application.setClubId(request.getClubId());
        application.setReason(request.getReason());
        application.setStatus(ApplicationStatusEnum.PENDING);
        application.setApplyTime(LocalDateTime.now());
        applicationRepository.save(application);

        // 新增：发送消息通知社团管理员（MANAGER）
        messageService.sendMessage(
                club.getCreatorId(), // 社团创建人ID（管理员）
                club.getId(),
                "有新的成员申请加入社团：" + club.getName(),
                MessageTypeEnum.APPLICATION
        );

        return ResultUtil.success();
    }

    @Override
    @Transactional
    public ApiResponse<Void> approveApplication(
            Long applicationId,
            Long clubId,
            Boolean isApproved,
            String feedback
    ) {
        // 校验申请记录是否存在且属于当前社团
        ClubApplication application = applicationRepository.findByIdAndClubId(applicationId, clubId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.APPLICATION_NOT_FOUND));

        // 校验申请是否已处理
        if (application.getStatus() != ApplicationStatusEnum.PENDING) {
            throw new BusinessException(ErrorCodeEnum.APPLICATION_ALREADY_HANDLED);
        }

        // 更新申请状态
        application.setStatus(isApproved ? ApplicationStatusEnum.APPROVED : ApplicationStatusEnum.REJECTED);
        application.setFeedback(feedback);
        applicationRepository.save(application);

        // 新增：发送消息通知申请人结果
        String content = isApproved
                ? "恭喜！您的社团申请已通过"
                : "您的社团申请未通过，原因：" + feedback;

        messageService.sendMessage(
                application.getUserId(), // 申请人ID
                clubId,
                content,
                MessageTypeEnum.APPLICATION
        );

        return ResultUtil.success();
    }
}