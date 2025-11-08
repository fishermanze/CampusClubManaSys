package com.club.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long receiverId; // 接收人ID
    private String content; // 消息内容
    private Integer type; // 1-社团申请通知，2-审批结果通知
    private Integer readStatus; // 0-未读，1-已读
    private LocalDateTime createTime; // 创建时间
}