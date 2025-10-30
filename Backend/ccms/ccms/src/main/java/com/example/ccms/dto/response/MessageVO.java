package com.example.ccms.dto.response;

import com.example.ccms.enums.MessageTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class MessageVO {
    private Long id;
    private String content;
    private MessageTypeEnum type;
    private Boolean isRead;
    private String createTime; // 前端需要字符串格式的时间
    private Long clubId;

    // 格式化 LocalDateTime 为字符串（可选，也可在转换时处理）
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}