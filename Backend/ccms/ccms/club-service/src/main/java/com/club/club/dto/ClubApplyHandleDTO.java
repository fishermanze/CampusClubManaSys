package com.club.club.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ClubApplyHandleDTO {
    @NotNull(message = "申请ID不能为空")
    private Long applyId;

    @NotNull(message = "处理结果不能为空")
    private Integer status; // 1-同意，2-拒绝
}