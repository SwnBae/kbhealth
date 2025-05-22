package kb.health.repository;

import kb.health.domain.notification.Notification;
import kb.health.domain.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //특정 알림 조회
    Optional<Notification> findByActorIdAndReceiverIdAndTypeAndRelatedId(Long actorId, Long receiverId, NotificationType type, Long relatedId);

    // 회원의 모든 알림 조회 (최신순)
    List<Notification> findByReceiverIdOrderByCreatedDateDesc(Long receiverId);

    // 페이지네이션 적용한 알림 조회
    Page<Notification> findByReceiverIdOrderByCreatedDateDesc(Long receiverId, Pageable pageable);

    // 읽지 않은 알림만 조회 (최신순)
    List<Notification> findByReceiverIdAndIsReadFalseOrderByCreatedDateDesc(Long receiverId);

    // 읽지 않은 알림 수 카운트 (서비스에서 성능 최적화를 위해 사용)
    long countByReceiverIdAndIsReadFalse(Long receiverId);

    // 특정 타입의 알림 조회
    List<Notification> findByReceiverIdAndTypeOrderByCreatedDateDesc(Long receiverId, NotificationType type);

    // 팔로우 알림 중복 체크 (한 사용자가 다른 사용자를 팔로우하는 알림)
    boolean existsByActorIdAndReceiverIdAndType(Long actorId, Long receiverId, NotificationType type);

    // 좋아요 알림 중복 체크 (한 게시물에 한 사용자의 좋아요는 한 번만)
    boolean existsByActorIdAndReceiverIdAndTypeAndRelatedId(
            Long actorId, Long receiverId, NotificationType type, Long relatedId);

    // 벌크 업데이트 - 사용자의 모든 알림 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = :receiverId AND n.isRead = false")
    int markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);

    // 벌크 삭제 - 사용자의 모든 알림 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.receiver.id = :receiverId")
    void deleteAllByReceiverId(@Param("receiverId") Long receiverId);

    // 특정 리소스와 관련된 알림 조회
    List<Notification> findByRelatedIdAndType(Long relatedId, NotificationType type);

    // 특정 사용자가 특정 게시물에 남긴 알림 조회
    List<Notification> findByActorIdAndRelatedIdAndType(
            Long actorId, Long relatedId, NotificationType type);
}