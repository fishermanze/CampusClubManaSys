package com.club.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("club")
public class Club {
    @TableId(type = IdType.AUTO)
    private Long id; // 社团ID
    private String name; // 社团名称
    private String description; // 社团描述
    private Long founderId; // 创始人ID
    private LocalDateTime createTime; // 创建时间
    private Integer status; // 状态：0-正常，1-禁用
}
