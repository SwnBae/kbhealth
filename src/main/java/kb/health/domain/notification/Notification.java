package kb.health.domain.notification;

import jakarta.persistence.*;
import kb.health.domain.BaseEntity;
import kb.health.domain.Member;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member receiver; // 알림을 받는 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Member actor; // 알림을 발생시킨 사용자

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String content; // 알림 내용

    private boolean isRead; // 읽음 여부

    @Column(name = "related_id")
    private Long relatedId = null; // 관련 엔티티의 ID (게시물 ID, 댓글 ID 등)

    /**
     * 연관관계 메서드
     */
    public void addReceiver(Member receiver) {
        receiver.getNotifications().add(this);
        this.receiver = receiver;
    }

    public void removeReceiver() {
        if (this.receiver != null) {
            this.receiver.getNotifications().remove(this);
            this.receiver = null;
        }
    }

    /**
     * 생성 메서드
     */

    // 알림 생성 정적 팩토리 메서드
    public static Notification createFollowNotification(Member actor, Member receiver) {
        Notification notification = Notification.builder()
                .actor(actor)
                .type(NotificationType.FOLLOW)
                .content(actor.getUserName() + "님이 회원님을 팔로우했습니다.")
                .isRead(false)
                .build();

        // 연관관계 설정 (양방향)
        notification.addReceiver(receiver);

        return notification;
    }


    public static Notification createCommentNotification(Member actor, Member receiver, Long commentId, String postTitle, String comment) {
        Notification notification = Notification.builder()
                .actor(actor)
                .type(NotificationType.COMMENT)
                .content(actor.getUserName() + "님이 회원님의 게시글 \"" + postTitle + "\"에 댓글을 남겼습니다." + "\n" + comment)
                .isRead(false)
                .relatedId(commentId)
                .build();

        // 연관관계 설정 (양방향)
        notification.addReceiver(receiver);

        return notification;
    }

    public static Notification createLikeNotification(Member actor, Member receiver, Long postId, String postTitle) {
        Notification notification = Notification.builder()
                .actor(actor)
                .type(NotificationType.LIKE)
                .content(actor.getUserName() + "님이 회원님의 게시글 \"" + postTitle + "\"을 좋아합니다.")
                .isRead(false)
                .relatedId(postId)
                .build();

        // 연관관계 설정 (양방향)
        notification.addReceiver(receiver);

        return notification;
    }

    // 알림 읽음 처리
    public void markAsRead() {
        this.isRead = true;
    }
}