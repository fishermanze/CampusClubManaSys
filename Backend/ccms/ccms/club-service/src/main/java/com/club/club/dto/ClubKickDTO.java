package com.club.club.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ClubKickDTO {
    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotNull(message = "被踢用户ID不能为空")
    private Long kickedUserId;
}