package com.example.ccms.controller;

import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.MessageVO;
import com.example.ccms.security.CurrentUser;
import com.example.ccms.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    /**
     * 查询用户消息列表（分页）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     */
    @GetMapping
    public ApiResponse<List<MessageVO>> getUserMessages(@CurrentUser Long userId,
                                                        @RequestParam(defaultValue = "1") int pageNum,
                                                        @RequestParam(defaultValue = "10") int pageSize) {
        return messageService.getUserMessages(userId, pageNum, pageSize);
    }

    /**
     * 查询未读消息数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(@CurrentUser Long userId) {
        return messageService.getUnreadCount(userId);
    }

    /**
     * 标记单条消息为已读
     */
    @PutMapping("/{messageId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long messageId, @CurrentUser Long userId) {
        return messageService.markAsRead(messageId, userId);
    }

    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(@CurrentUser Long userId) {
        return messageService.markAllAsRead(userId);
    }
}