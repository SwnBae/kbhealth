package kb.health.service;

import kb.health.domain.Member;
import kb.health.domain.notification.Notification;
import kb.health.domain.notification.NotificationType;
import kb.health.exception.NotificationException;
import kb.health.repository.MemberRepository;
import kb.health.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    /**
     * 알림 조회 관련
     */
    // 회원의 모든 알림 조회
    public List<Notification> getNotifications(Long memberId) {
        return notificationRepository.findByReceiverIdOrderByCreatedDateDesc(memberId);
    }

    public Page<Notification> getPagedNotifications(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByReceiverIdOrderByCreatedDateDesc(memberId, pageable);
    }

    // 읽지 않은 알림만 조회
    public List<Notification> getUnreadNotifications(Long memberId) {
        return notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedDateDesc(memberId);
    }

    // 읽지 않은 알림 개수 조회 (성능 최적화)
    public long countUnreadNotifications(Long memberId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(memberId);
    }

    /**
     * 알림 생성 관련
     */
    // 팔로우 알림 생성 (중복 체크)
    @Transactional
    public Notification createFollowNotification(Long actorId, Long receiverId) {
        // 자기 자신에게는 알림을 보내지 않음
        if (isSelf(actorId, receiverId)) {
            return null;
        }

        // 중복 알림 체크
        if (notificationRepository.existsByActorIdAndReceiverIdAndType(
                actorId, receiverId, NotificationType.FOLLOW)) {
            return null; // 이미 알림이 존재하면 생성하지 않음
        }

        Member actor = memberRepository.findMemberById(actorId);
        Member receiver = memberRepository.findMemberById(receiverId);

        Notification notification = Notification.createFollowNotification(actor, receiver);
        return notificationRepository.save(notification);
    }

    // 댓글 알림 생성 (중복 체크 없음 - 모든 댓글에 알림 생성)
    @Transactional
    public Notification createCommentNotification(Long actorId, Long receiverId, Long commentId, String postTitle, String comment) {
        // 자기 자신에게는 알림을 보내지 않음
        if (isSelf(actorId, receiverId)) {
            return null;
        }

        Member actor = memberRepository.findMemberById(actorId);
        Member receiver = memberRepository.findMemberById(receiverId);

        Notification notification = Notification.createCommentNotification(actor, receiver, commentId, postTitle, comment);
        return notificationRepository.save(notification);
    }

    // 좋아요 알림 생성 (중복 체크)
    @Transactional
    public Notification createLikeNotification(Long actorId, Long receiverId, Long postId, String postTitle) {
        // 자기 자신에게는 알림을 보내지 않음
        if (isSelf(actorId, receiverId)) {
            return null;
        }

        // 중복 알림 체크
        if (notificationRepository.existsByActorIdAndReceiverIdAndTypeAndRelatedId(
                actorId, receiverId, NotificationType.LIKE, postId)) {
            return null; // 이미 알림이 존재하면 생성하지 않음
        }

        Member actor = memberRepository.findMemberById(actorId);
        Member receiver = memberRepository.findMemberById(receiverId);

        Notification notification = Notification.createLikeNotification(actor, receiver, postId, postTitle);
        return notificationRepository.save(notification);
    }

    /**
     * 알림 상태 관리
     */
    // 단일 알림 읽음 처리
    @Transactional
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException::notificationNotFound);

        notification.markAsRead();
    }

    // 여러 알림 읽음 처리
    @Transactional
    public void readNotifications(List<Long> notificationIds) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);

        for (Notification notification : notifications) {
            notification.markAsRead();
        }
        // 더티 체킹으로 자동 업데이트
    }

    // 사용자의 모든 알림 읽음 처리 (벌크 연산)
    @Transactional
    public int readAllNotifications(Long memberId) {
        return notificationRepository.markAllAsReadByReceiverId(memberId);
    }

    // 알림 삭제
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException::notificationNotFound);

        // 양방향 연관관계 정리
        notification.removeReceiver();

        notificationRepository.delete(notification);
    }

    // 내부 로직용 알림 삭제
    @Transactional
    public void deleteNotification(Long actorId, Long receiverId, NotificationType notificationType, Long relativeId){
        // 자신일 경우 쿼리 조회 X
        if (isSelf(actorId, receiverId)) {
            return;
        }

        notificationRepository.findByActorIdAndReceiverIdAndTypeAndRelatedId(actorId, receiverId, notificationType, relativeId)
                .ifPresent(notification -> {
                    notification.removeReceiver();
                    notificationRepository.delete(notification);
                });
    }

    // 사용자의 모든 알림 삭제 (벌크 연산)
    @Transactional
    public void deleteAllNotifications(Long memberId) {
        notificationRepository.deleteAllByReceiverId(memberId);
    }

    /**
     * 내부 메서드
     */

    private static boolean isSelf(Long currentMemberId, Long targetId) {
        return currentMemberId.equals(targetId);
    }
}