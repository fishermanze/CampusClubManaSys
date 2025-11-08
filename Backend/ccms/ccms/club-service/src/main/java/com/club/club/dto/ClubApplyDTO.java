package com.club.club.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ClubApplyDTO {
    @NotNull(message = "社团ID不能为空")
    private Long clubId;
}