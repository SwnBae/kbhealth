package kb.health.service;

import kb.health.controller.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealTimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(Long userId, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }

    public void sendNotificationCountToUser(Long userId, long count) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification-count",
                count
        );
    }
}
