package com.club.activity.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ActivityCreateDTO {
    @NotNull(message = "社团ID不能为空")
    private Long clubId;
    @NotNull(message = "活动标题不能为空")
    private String title;
    @NotNull(message = "活动内容不能为空")
    private String content;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    @NotNull(message = "活动地点不能为空")
    private String location;
}