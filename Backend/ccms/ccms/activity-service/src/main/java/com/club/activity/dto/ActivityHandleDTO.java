package com.club.activity.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ActivityHandleDTO {
    @NotNull(message = "申请ID不能为空")
    private Long applyId;
    @NotNull(message = "处理结果不能为空（1-同意，2-拒绝）")
    private Integer status;
}