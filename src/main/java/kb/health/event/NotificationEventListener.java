package kb.health.event;

import kb.health.controller.response.NotificationResponse;
import kb.health.domain.notification.Notification;
import kb.health.service.NotificationService;
import kb.health.service.RealTimeNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final RealTimeNotificationService realTimeNotificationService;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationTaskExecutor") // 비동기 처리
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        try {
            log.info("🔔 알림 이벤트 수신: notificationId={}, receiverId={}",
                    event.getNotificationId(), event.getReceiverId());

            // 1. 알림 정보 조회 (트랜잭션 내에서 연관 엔티티까지 로딩)
            Notification notification = notificationService.getNotificationById(event.getNotificationId());

            // 🔥 Lazy Loading 강제 실행 (트랜잭션 내에서)
            String actorName = notification.getActor().getUserName(); // 강제 로딩
            String receiverName = notification.getReceiver().getUserName(); // 강제 로딩
            log.debug("알림 당사자: {} -> {}", actorName, receiverName);

            // 2. DTO 변환 (이제 모든 데이터가 로딩된 상태)
            NotificationResponse response = NotificationResponse.create(notification);

            // 3. 실시간 알림 전송
            realTimeNotificationService.sendNotificationToUser(event.getReceiverId(), response);
            log.info("✅ 실시간 알림 전송 완료: receiverId={}", event.getReceiverId());

            // 4. 읽지 않은 알림 개수 업데이트 및 전송
            long unreadCount = notificationService.countUnreadNotifications(event.getReceiverId());
            realTimeNotificationService.sendNotificationCountToUser(event.getReceiverId(), unreadCount);
            log.info("🔢 실시간 알림 개수 전송 완료: receiverId={}, count={}",
                    event.getReceiverId(), unreadCount);

        } catch (Exception e) {
            log.error("❌ 실시간 알림 전송 실패: notificationId={}, error={}",
                    event.getNotificationId(), e.getMessage(), e);
            // 실시간 전송 실패해도 알림 생성은 성공적으로 완료됨
        }
    }
}
