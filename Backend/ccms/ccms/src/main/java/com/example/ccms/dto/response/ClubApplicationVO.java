package com.example.ccms.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团申请视图对象
 */
@Data
public class ClubApplicationVO {
    private Long id;
    private Long userId;
    private String username;
    private String realName;
    private Long clubId;
    private String clubName;
    private String reason;
    private String status; // 存储枚举的name()，如"PENDING"
    private String feedback;
    private LocalDateTime applyTime;
    private LocalDateTime handleTime;
}