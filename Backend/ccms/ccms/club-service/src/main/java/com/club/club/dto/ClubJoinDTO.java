package com.club.club.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ClubJoinDTO {
    @NotNull(message = "社团ID不能为空")
    private Long clubId;
}