package com.club.club.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ClubCreateDTO {
    @NotBlank(message = "社团名称不能为空")
    @Size(max = 50, message = "社团名称长度不能超过50字")
    private String name;

    @Size(max = 500, message = "社团描述长度不能超过500字")
    private String description;
}