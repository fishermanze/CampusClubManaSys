package com.club.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("club_member")
public class ClubMember {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID
    private Long clubId; // 社团ID
    private Long userId; // 用户ID
    private Integer role; // 角色：0-普通成员，1-管理员，2-创始人
    private LocalDateTime joinTime; // 加入时间
}