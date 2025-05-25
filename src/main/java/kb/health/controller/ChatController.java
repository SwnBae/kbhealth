package kb.health.controller;

import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.response.ChatMessageResponse;
import kb.health.controller.response.ChatRoomInfo;
import kb.health.controller.response.ChatRoomResponse;
import kb.health.domain.chat.ChatMessage;
import kb.health.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    // 웹소켓을 통한 메시지 전송 (Principal 유지 - 웹소켓에서는 @LoginMember 사용 불가)
    @MessageMapping("/send-message")
    public void sendMessage(@Payload Map<String, Object> message, Principal principal) {
        Long senderId = Long.parseLong(principal.getName());
        Long receiverId = Long.parseLong(message.get("receiverId").toString());
        String content = message.get("content").toString();

        chatService.sendMessage(senderId, receiverId, content);
    }

    // REST API로 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@LoginMember CurrentMember currentMember) {
        List<ChatRoomInfo> chatRooms = chatService.getChatRooms(currentMember.getId());

        // ChatRoomInfo를 ChatRoomResponse로 변환
        List<ChatRoomResponse> responseList = chatRooms.stream()
                .map(ChatRoomResponse::create)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    // REST API로 채팅 메시지 조회
    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable String chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<ChatMessage> messages = chatService.getChatMessages(chatRoomId, page, size);

        // ChatMessage를 ChatMessageResponse로 변환
        List<ChatMessageResponse> responseList = messages.getContent().stream()
                .map(ChatMessageResponse::create)
                .collect(Collectors.toList());

        // 새로운 Page 객체 생성
        Page<ChatMessageResponse> responsePage = new PageImpl<>(
                responseList,
                messages.getPageable(),
                messages.getTotalElements()
        );

        return ResponseEntity.ok(responsePage);
    }

    // 메시지 읽음 처리
    @PostMapping("/rooms/{chatRoomId}/read")
    public ResponseEntity<String> markMessagesAsRead(
            @PathVariable String chatRoomId,
            @LoginMember CurrentMember currentMember) {
        chatService.markMessagesAsRead(chatRoomId, currentMember.getId());
        return ResponseEntity.ok("메시지를 읽음 처리했습니다.");
    }

    // 읽지 않은 메시지 총 개수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getTotalUnreadCount(@LoginMember CurrentMember currentMember) {
        long unreadCount = chatService.getTotalUnreadCount(currentMember.getId());
        return ResponseEntity.ok(unreadCount);
    }
}
