package com.club.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.club.message.entity.Message;
import java.util.List;

public interface MessageService extends IService<Message> {

    /**
     * 保存消息到数据库
     * @param receiverId 接收人ID
     * @param content 消息内容
     * @param type 消息类型（1-申请通知，2-审批结果）
     */
    void saveMessage(Long receiverId, String content, Integer type);

    /**
     * 分页查询用户消息
     * @param page 分页参数
     * @param receiverId 接收人ID
     * @return 分页消息列表
     */
    IPage<Message> getUserMessages(Page<Message> page, Long receiverId);

    /**
     * 标记消息为已读
     * @param messageIds 消息ID列表
     */
    void markAsRead(List<Long> messageIds);
}