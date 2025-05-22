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
        String destination = "/user/" + userId + "/queue/notifications";
        messagingTemplate.convertAndSend(destination, notification);
    }

    public void sendNotificationCountToUser(Long userId, Long count) {
        String destination = "/user/" + userId + "/queue/notification-count";
        messagingTemplate.convertAndSend(destination, count);
    }

    public void sendNotificationListUpdate(Long userId, Map<String, Object> updateData) {
        String destination = "/user/" + userId + "/queue/notification-list-update";
        messagingTemplate.convertAndSend(destination, updateData);
    }

    public void sendGlobalNotification(Object notification) {
        String destination = "/topic/global-notifications";
        messagingTemplate.convertAndSend(destination, notification);
    }

    public void sendGroupNotification(String groupId, Object notification) {
        String destination = "/topic/group/" + groupId;
        messagingTemplate.convertAndSend(destination, notification);
    }
}