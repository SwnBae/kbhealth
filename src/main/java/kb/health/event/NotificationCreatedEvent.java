package kb.health.event;

import kb.health.domain.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationCreatedEvent {
    private final Long notificationId;
    private final Long receiverId;
    private final NotificationType type;
}
