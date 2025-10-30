package com.example.ccms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 社团申请请求参数：接收申请加入社团的必要信息
 */
@Data
public class ClubApplyRequest {

    @NotNull(message = "社团ID不能为空")
    private Long clubId; // 申请加入的社团ID

    @NotBlank(message = "申请理由不能为空")
    @Size(min = 10, max = 500, message = "申请理由长度必须为10-500字")
    private String reason; // 申请加入的理由（说明加入社团的目的）
}