package kb.health.domain.notification;

public enum NotificationType {
    FOLLOW,     // 팔로우 알림
    COMMENT,    // 댓글 알림
    LIKE,       // 좋아요 알림
    SYSTEM      // 시스템 알림 (관리자가 보내는 알림 등)
}