package kb.health.service;

import kb.health.domain.Member;
import kb.health.domain.notification.Notification;
import kb.health.domain.notification.NotificationType;
import kb.health.event.NotificationCreatedEvent;
import kb.health.exception.NotificationException;
import kb.health.repository.MemberRepository;
import kb.health.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RealTimeNotificationService realTimeNotificationService;

    /**
     * 알림 조회 관련
     */
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException::notificationNotFound);
    }

    public List<Notification> getNotifications(Long memberId) {
        return notificationRepository.findByReceiverIdOrderByCreatedDateDesc(memberId);
    }

    public Page<Notification> getPagedNotifications(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByReceiverIdOrderByCreatedDateDesc(memberId, pageable);
    }

    public List<Notification> getUnreadNotifications(Long memberId) {
        return notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedDateDesc(memberId);
    }

    public long countUnreadNotifications(Long memberId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(memberId);
    }

    /**
     * 알림 생성 관련
     */
    @Transactional
    public Notification createFollowNotification(Long actorId, Long receiverId) {
        if (isSelf(actorId, receiverId)) {
            return null;
        }

        if (notificationRepository.existsByActorIdAndReceiverIdAndType(
                actorId, receiverId, NotificationType.FOLLOW)) {
            return null;
        }

        Member actor = memberRepository.findMemberById(actorId);
        Member receiver = memberRepository.findMemberById(receiverId);

        Notification notification = Notification.createFollowNotification(actor, receiver);
        Notification savedNotification = notificationRepository.save(notification);

        eventPublisher.publishEvent(new NotificationCreatedEvent(
                savedNotification.getId(),
                receiverId,
                NotificationType.FOLLOW
        ));

        return savedNotification;
    }

    @Transactional
    public Notification createCommentNotification(Long actorId, Long receiverId, Long commentId, String postTitle, String comment) {
        if (isSelf(actorId, receiverId)) {
            return null;
        }

        Member actor = memberRepository.findMemberById(actorId);
        Member receiver = memberRepository.findMemberById(receiverId);

        Notification notification = Notification.createCommentNotification(actor, receiver, commentId, postTitle, comment);
        Notification savedNotification = notificationRepository.save(notification);

        eventPublisher.publishEvent(new NotificationCreatedEvent(
                savedNotification.getId(),
                receiverId,
                NotificationType.COMMENT
        ));

        return savedNotification;
    }

    @Transactional
    public Notification createLikeNotification(Long actorId, Long receiverId, Long postId, String postTitle) {
        if (isSelf(actorId, receiverId)) {
            return null;
        }

        if (notificationRepository.existsByActorIdAndReceiverIdAndTypeAndRelatedId(
                actorId, receiverId, NotificationType.LIKE, postId)) {
            return null;
        }

        Member actor = memberRepository.findMemberById(actorId);
        Member receiver = memberRepository.findMemberById(receiverId);

        Notification notification = Notification.createLikeNotification(actor, receiver, postId, postTitle);
        Notification savedNotification = notificationRepository.save(notification);

        eventPublisher.publishEvent(new NotificationCreatedEvent(
                savedNotification.getId(),
                receiverId,
                NotificationType.LIKE
        ));

        return savedNotification;
    }

    /**
     * 알림 상태 관리 - 🔥 실시간 개수 업데이트 추가
     */
    @Transactional
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException::notificationNotFound);

        // 🔥 이미 읽은 알림이면 개수 업데이트 불필요
        if (notification.isRead()) {
            return;
        }

        notification.markAsRead();

        // 🔥 읽음 처리 후 개수 실시간 업데이트
        Long receiverId = notification.getReceiver().getId();
        sendNotificationCountUpdate(receiverId);
    }

    @Transactional
    public void readNotifications(List<Long> notificationIds) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);

        Long receiverId = null;
        boolean hasUpdates = false;

        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.markAsRead();
                receiverId = notification.getReceiver().getId();
                hasUpdates = true;
            }
        }

        // 🔥 변경사항이 있으면 개수 업데이트
        if (hasUpdates && receiverId != null) {
            sendNotificationCountUpdate(receiverId);
        }
    }

    @Transactional
    public int readAllNotifications(Long memberId) {
        int count = notificationRepository.markAllAsReadByReceiverId(memberId);

        // 🔥 읽음 처리된 알림이 있으면 개수 업데이트
        if (count > 0) {
            sendNotificationCountUpdate(memberId);
        }

        return count;
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException::notificationNotFound);

        Long receiverId = notification.getReceiver().getId();
        boolean wasUnread = !notification.isRead();

        notification.removeReceiver();
        notificationRepository.delete(notification);

        // 🔥 읽지 않은 알림이 삭제되었으면 개수 업데이트
        if (wasUnread) {
            sendNotificationCountUpdate(receiverId);
        }
    }

    @Transactional
    public void deleteNotification(Long actorId, Long receiverId, NotificationType notificationType, Long relativeId) {
        if (isSelf(actorId, receiverId)) {
            return;
        }

        notificationRepository.findByActorIdAndReceiverIdAndTypeAndRelatedId(actorId, receiverId, notificationType, relativeId)
                .ifPresent(notification -> {
                    boolean wasUnread = !notification.isRead();
                    notification.removeReceiver();
                    notificationRepository.delete(notification);

                    // 🔥 읽지 않은 알림이 삭제되었으면 개수 업데이트
                    if (wasUnread) {
                        sendNotificationCountUpdate(receiverId);
                    }
                });
    }

    @Transactional
    public void deleteAllNotifications(Long memberId) {
        // 🔥 삭제 전 읽지 않은 알림이 있는지 확인
        long unreadCount = countUnreadNotifications(memberId);

        notificationRepository.deleteAllByReceiverId(memberId);

        // 🔥 읽지 않은 알림이 있었으면 개수 업데이트
        if (unreadCount > 0) {
            sendNotificationCountUpdate(memberId);
        }
    }

    /**
     * 🔥 실시간 알림 개수 업데이트 전송
     */
    private void sendNotificationCountUpdate(Long userId) {
        try {
            long unreadCount = countUnreadNotifications(userId);
            realTimeNotificationService.sendNotificationCountToUser(userId, unreadCount);
            log.info("🔢 알림 개수 업데이트 전송: userId={}, count={}", userId, unreadCount);
        } catch (Exception e) {
            log.warn("⚠️ 알림 개수 업데이트 전송 실패: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 내부 메서드
     */
    private static boolean isSelf(Long currentMemberId, Long targetId) {
        return currentMemberId.equals(targetId);
    }
}