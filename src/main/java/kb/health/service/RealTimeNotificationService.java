package kb.health.service;

import kb.health.controller.response.ChatMessageResponse;
import kb.health.domain.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RealTimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // 알림 관련 메서드들
    public void sendNotificationToUser(Long userId, Object notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }

    public void sendNotificationCountToUser(Long userId, Long count) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification-count",
                count
        );
    }

    public void sendNotificationListUpdate(Long userId, Map<String, Object> updateData) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification-list-update",
                updateData
        );
    }

    // 채팅 관련 메서드들
    public void sendChatMessage(Long userId, ChatMessage message) {
        ChatMessageResponse response = ChatMessageResponse.create(message);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/chat-messages",
                response  // Response 객체로 전송
        );
    }

    public void sendChatRoomUpdate(Long userId) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/chat-room-update",
                "UPDATE"
        );
    }

    // 채팅 개수 전용 메서드
    public void sendChatUnreadCount(Long userId, Long count) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/chat-unread-count",
                count
        );
    }

    public void sendMessageReadStatus(Long senderId, String chatRoomId) {
        Map<String, Object> readStatus = Map.of(
                "chatRoomId", chatRoomId,
                "isRead", true
        );

        messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/message-read-status",
                readStatus
        );
    }

    /**
     * 예비 공지 메서드
     */
    // 전역 알림 - 현재 방식이 올바름
    public void sendGlobalNotification(Object notification) {
        messagingTemplate.convertAndSend("/topic/global-notifications", notification);
    }

    // 그룹 알림 - 현재 방식이 올바름
    public void sendGroupNotification(String groupId, Object notification) {
        messagingTemplate.convertAndSend("/topic/group/" + groupId, notification);
    }
}