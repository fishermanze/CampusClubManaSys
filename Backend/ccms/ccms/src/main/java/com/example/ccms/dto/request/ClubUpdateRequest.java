package com.example.ccms.dto.request;

import com.example.ccms.enums.RecruitStatusEnum;
import lombok.Data;

@Data
public class ClubUpdateRequest {
    private String name;
    private String direction;
    private String introduction;
    private Integer maxNumber; // 允许更新的字段
    private RecruitStatusEnum recruitStatus;
}