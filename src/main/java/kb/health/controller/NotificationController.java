package kb.health.controller;

import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.response.NotificationResponse;
import kb.health.domain.feed.Comment;
import kb.health.domain.feed.Post;
import kb.health.domain.notification.Notification;
import kb.health.domain.notification.NotificationType;
import kb.health.service.FeedService;
import kb.health.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final FeedService feedService;

    // 페이징된 알림 조회 - NotificationResponse로 반환 타입 변경
    @GetMapping("/paged")
    public ResponseEntity<Page<NotificationResponse>> getPagedNotifications(
            @LoginMember CurrentMember currentMember,
            @RequestParam int page,
            @RequestParam int size) {
        Page<Notification> notifications = notificationService.getPagedNotifications(currentMember.getId(), page, size);

        // 엔티티를 DTO로 변환
        List<NotificationResponse> responseList = new ArrayList<>();

        for(Notification notification : notifications.getContent()) {
            if(notification.getType().equals(NotificationType.COMMENT)){
                Comment comment = feedService.getCommentWithPost(notification.getRelatedId())
                        .orElseThrow(() -> new IllegalArgumentException("댓글 찾지 못함"));
                Post post = comment.getPost();

                NotificationResponse notificationResponse = NotificationResponse.create(notification,post.getId());
                responseList.add(notificationResponse);
            } else{
                NotificationResponse notificationResponse = NotificationResponse.create(notification);
                responseList.add(notificationResponse);
            }
        }
//        List<NotificationResponse> responseList = notifications.getContent().stream()
//                .map(NotificationResponse::create)
//                .collect(Collectors.toList());

        // 새로운 Page 객체 생성
        Page<NotificationResponse> responsePage = new PageImpl<>(
                responseList,
                notifications.getPageable(),
                notifications.getTotalElements()
        );

        return ResponseEntity.ok(responsePage);
    }

    // 단일 알림 읽음 처리
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> readNotification(
            @LoginMember CurrentMember currentMember,
            @PathVariable Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.ok("알림을 읽음 처리했습니다.");
    }

    // 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @LoginMember CurrentMember currentMember,
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("알림을 삭제했습니다.");
    }

    /**
     * 아래는 사용예정 or 사용하지 않을 메서드 - NotificationResponse로 반환 타입 변경
     */
    // 전체 알림 조회
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@LoginMember CurrentMember currentMember) {
        List<Notification> notifications = notificationService.getNotifications(currentMember.getId());
        List<NotificationResponse> responseList = notifications.stream()
                .map(NotificationResponse::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // 읽지 않은 알림만 조회
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@LoginMember CurrentMember currentMember) {
        List<Notification> notifications = notificationService.getUnreadNotifications(currentMember.getId());
        List<NotificationResponse> responseList = notifications.stream()
                .map(NotificationResponse::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@LoginMember CurrentMember currentMember) {
        long count = notificationService.countUnreadNotifications(currentMember.getId());
        return ResponseEntity.ok(count);
    }

    // 여러 알림 읽음 처리
    @PutMapping("/read")
    public ResponseEntity<?> readNotifications(
            @LoginMember CurrentMember currentMember,
            @RequestBody List<Long> notificationIds) {
        notificationService.readNotifications(notificationIds);
        return ResponseEntity.ok("여러 알림을 읽음 처리했습니다.");
    }

    // 모든 알림 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<?> readAllNotifications(@LoginMember CurrentMember currentMember) {
        int count = notificationService.readAllNotifications(currentMember.getId());
        return ResponseEntity.ok(count + "개의 알림을 읽음 처리했습니다.");
    }

    // 모든 알림 삭제
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllNotifications(@LoginMember CurrentMember currentMember) {
        notificationService.deleteAllNotifications(currentMember.getId());
        return ResponseEntity.ok("모든 알림을 삭제했습니다.");
    }
}