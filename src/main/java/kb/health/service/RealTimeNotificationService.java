package kb.health.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RealTimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

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

    // 전역 알림 - 현재 방식이 올바름
    public void sendGlobalNotification(Object notification) {
        messagingTemplate.convertAndSend("/topic/global-notifications", notification);
    }

    // 그룹 알림 - 현재 방식이 올바름
    public void sendGroupNotification(String groupId, Object notification) {
        messagingTemplate.convertAndSend("/topic/group/" + groupId, notification);
    }
}