package kb.health.service;

import kb.health.domain.Member;
import kb.health.domain.notification.Notification;
import kb.health.domain.notification.NotificationType;
import kb.health.event.NotificationCreatedEvent;
import kb.health.exception.NotificationException;
import kb.health.repository.MemberRepository;
import kb.health.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RealTimeNotificationService realTimeNotificationService;

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

    @Transactional
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException::notificationNotFound);

        if (notification.isRead()) {
            return;
        }

        notification.markAsRead();
        Long receiverId = notification.getReceiver().getId();

        sendListUpdate(receiverId, "READ", notificationId, null);
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

                sendListUpdate(receiverId, "READ", notification.getId(), null);
            }
        }

        if (hasUpdates && receiverId != null) {
            sendNotificationCountUpdate(receiverId);
        }
    }

    @Transactional
    public int readAllNotifications(Long memberId) {
        int count = notificationRepository.markAllAsReadByReceiverId(memberId);

        if (count > 0) {
            sendListUpdate(memberId, "READ_ALL", null, null);
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

        sendListUpdate(receiverId, "DELETE", notificationId, null);

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
                    Long notificationId = notification.getId();

                    notification.removeReceiver();
                    notificationRepository.delete(notification);

                    sendListUpdate(receiverId, "DELETE", notificationId, null);

                    if (wasUnread) {
                        sendNotificationCountUpdate(receiverId);
                    }
                });
    }

    @Transactional
    public void deleteAllNotifications(Long memberId) {
        long unreadCount = countUnreadNotifications(memberId);

        notificationRepository.deleteAllByReceiverId(memberId);

        sendListUpdate(memberId, "DELETE_ALL", null, null);

        if (unreadCount > 0) {
            sendNotificationCountUpdate(memberId);
        }
    }

    private void sendListUpdate(Long userId, String updateType, Long notificationId, Object notification) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("type", updateType);
        updateData.put("notificationId", notificationId);
        updateData.put("notification", notification);

        realTimeNotificationService.sendNotificationListUpdate(userId, updateData);
    }

    private void sendNotificationCountUpdate(Long userId) {
        long unreadCount = countUnreadNotifications(userId);
        realTimeNotificationService.sendNotificationCountToUser(userId, unreadCount);
    }

    private static boolean isSelf(Long currentMemberId, Long targetId) {
        return currentMemberId.equals(targetId);
    }
}