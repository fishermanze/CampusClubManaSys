package com.example.ccms.dto.request;

import com.example.ccms.enums.RecruitStatusEnum;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class ClubCreateRequest {
    @NotEmpty
    private String name;

    private String direction;

    @NotEmpty
    private String introduction;

    @NotNull
    private Integer maxNumber;

    private RecruitStatusEnum recruitStatus = RecruitStatusEnum.OPEN;

    // 新增：社团主图URL（前端上传后传递的URL）
    private String imageUrl;
}