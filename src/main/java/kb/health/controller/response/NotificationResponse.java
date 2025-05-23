package kb.health.controller.response;

import kb.health.domain.notification.Notification;
import kb.health.domain.notification.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long notificationId;
    private NotificationType type;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    private Long actorId;
    private String actorName;
    private String actorAccount;
    private String actorProfileImage;
    private Long relatedId;
    private Long relatedPostId;

    // 엔티티를 Response DTO로 변환하는 정적 팩토리 메서드
    public static NotificationResponse create(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .type(notification.getType())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedDate())
                .actorId(notification.getActor() != null ? notification.getActor().getId() : null)
                .actorName(notification.getActor() != null ? notification.getActor().getUserName() : null)
                .actorAccount(notification.getActor() != null ? notification.getActor().getAccount() : null)
                .actorProfileImage(notification.getActor() != null ? notification.getActor().getProfileImageUrl() : null)
                .relatedId(notification.getRelatedId())
                .build();
    }

    public static NotificationResponse create(Notification notification, Long relatedPostId){
        NotificationResponse notificationResponse = create(notification);
        notificationResponse.relatedPostId = relatedPostId;
        return notificationResponse;
    }
}
