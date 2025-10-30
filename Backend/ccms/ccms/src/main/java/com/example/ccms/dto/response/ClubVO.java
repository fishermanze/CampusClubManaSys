package com.example.ccms.dto.response;

import com.example.ccms.enums.RecruitStatusEnum;
import lombok.Data;

@Data // 自动生成 getter/setter
public class ClubVO {
    private Long id;
    private String name;
    private String direction;
    private String introduction;
    private Integer maxNumber; // 与实体类对应
    private RecruitStatusEnum recruitStatus;
    private String mainImageUrl;
}