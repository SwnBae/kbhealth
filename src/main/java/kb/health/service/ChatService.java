package kb.health.service;

import kb.health.controller.response.ChatRoomInfo;
import kb.health.domain.Member;
import kb.health.domain.chat.ChatMessage;
import kb.health.repository.ChatMessageRepository;
import kb.health.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final RealTimeNotificationService realTimeNotificationService;

    // 메시지 전송 - 개수 업데이트 포함
    @Transactional
    public ChatMessage sendMessage(Long senderId, Long receiverId, String content) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ChatMessage chatMessage = ChatMessage.create(sender, receiver, content);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 수신자에게 실시간 메시지 전송
        realTimeNotificationService.sendChatMessage(receiverId, savedMessage);

        // 송신자에게 실시간 메시지 전송 (본인이 보낸 메시지 확인용)
        realTimeNotificationService.sendChatMessage(senderId, savedMessage);

        // 채팅방 목록 업데이트를 위해 양쪽 사용자에게 알림
        realTimeNotificationService.sendChatRoomUpdate(senderId);
        realTimeNotificationService.sendChatRoomUpdate(receiverId);

        // 채팅 개수 업데이트 전송 (수신자에게만)
        long receiverUnreadCount = getTotalUnreadCount(receiverId);
        realTimeNotificationService.sendChatUnreadCount(receiverId, receiverUnreadCount);

        return savedMessage;
    }

    // 채팅방 메시지 조회
    public Page<ChatMessage> getChatMessages(String chatRoomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatMessageRepository.findByChatRoomIdOrderByCreatedDateDesc(chatRoomId, pageable);
    }

    // 사용자의 채팅방 목록 조회 (Map 형태 반환)
    public List<ChatRoomInfo> getChatRooms(Long userId) {
        List<String> chatRoomIds = chatMessageRepository.findChatRoomIdsByUserId(userId);
        List<ChatRoomInfo> chatRooms = new ArrayList<>();

        for (String chatRoomId : chatRoomIds) {
            ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomIdOrderByCreatedDateDesc(chatRoomId);
            if (lastMessage != null) {
                // 상대방 정보 가져오기
                Member partner = getPartner(lastMessage, userId);
                long unreadCount = chatMessageRepository.countByChatRoomIdAndReceiverIdAndIsReadFalse(chatRoomId, userId);

                ChatRoomInfo chatRoom = new ChatRoomInfo(
                        chatRoomId,
                        partner,
                        lastMessage,
                        unreadCount
                );

                chatRooms.add(chatRoom);
            }
        }

        // 최신 메시지 순으로 정렬
        return chatRooms.stream()
                .sorted((a, b) -> b.getLastMessage().getCreatedDate().compareTo(a.getLastMessage().getCreatedDate()))
                .collect(Collectors.toList());
    }

    // 메시지 읽음 처리 - 개수 업데이트 포함
    @Transactional
    public void markMessagesAsRead(String chatRoomId, Long userId) {
        int updatedCount = chatMessageRepository.markMessagesAsRead(chatRoomId, userId);

        if (updatedCount > 0) {
            // 채팅방 목록 업데이트 알림
            realTimeNotificationService.sendChatRoomUpdate(userId);

            // 채팅 개수 업데이트 전송
            long unreadCount = getTotalUnreadCount(userId);
            realTimeNotificationService.sendChatUnreadCount(userId, unreadCount);

            // 메시지 보낸 사람에게 읽음 알림 전송
            Long partnerId = getPartnerId(chatRoomId, userId);
            realTimeNotificationService.sendMessageReadStatus(partnerId, chatRoomId);
        }
    }

    // 읽지 않은 메시지 총 개수 조회
    public long getTotalUnreadCount(Long userId) {
        List<String> chatRoomIds = chatMessageRepository.findChatRoomIdsByUserId(userId);
        return chatRoomIds.stream()
                .mapToLong(chatRoomId -> chatMessageRepository.countByChatRoomIdAndReceiverIdAndIsReadFalse(chatRoomId, userId))
                .sum();
    }

    private Member getPartner(ChatMessage message, Long currentUserId) {
        return message.getSender().getId().equals(currentUserId)
                ? message.getReceiver()
                : message.getSender();
    }

    private Long getPartnerId(String chatRoomId, Long currentUserId) {
        String[] userIds = chatRoomId.split("_");
        Long userId1 = Long.parseLong(userIds[0]);
        Long userId2 = Long.parseLong(userIds[1]);

        return userId1.equals(currentUserId) ? userId2 : userId1;
    }
}