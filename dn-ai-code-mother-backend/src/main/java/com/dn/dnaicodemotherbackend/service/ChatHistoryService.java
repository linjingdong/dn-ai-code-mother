package com.dn.dnaicodemotherbackend.service;

import com.dn.dnaicodemotherbackend.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.dn.dnaicodemotherbackend.model.entity.ChatHistory;
import com.dn.dnaicodemotherbackend.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author Lin_jd
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     *
     * @param appId       应用 ID
     * @param message     对话消息
     * @param messageType 消息类型
     * @param userId      用户 ID
     * @return 布尔值
     */
    boolean addChatHistory(Long appId, String message, String messageType, Long userId);

    /**
     * 删除对话历史
     * @param appId 应用 ID
     * @return 布尔值
     */
    boolean deleteByAppId(Long appId);

    /**
     * 查询对话历史
     * @param appId 应用 ID
     * @param pageSize 页数
     * @param lastCreateTime 游标（创建时间）
     * @param loginUser 登录用户
     * @return 对话历史
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    /**
     * 加载对话历史到对话记忆中
     * @param appId 应用 ID
     * @param chatMemory 对话记忆
     * @param maxCount 最大对话历史条数
     * @return 加载条数
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

    /**
     * 构造查询条件
     * @param chatHistoryQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);
}
