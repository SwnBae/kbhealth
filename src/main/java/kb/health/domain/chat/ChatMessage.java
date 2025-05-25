package kb.health.domain.chat;

import jakarta.persistence.*;
import kb.health.domain.BaseEntity;
import kb.health.domain.Member;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Column(nullable = false, length = 1000)
    private String content;

    private boolean isRead = false;

    // 채팅방 ID (두 사용자의 ID를 조합하여 생성)
    @Column(name = "chat_room_id", nullable = false)
    private String chatRoomId;

    public static ChatMessage create(Member sender, Member receiver, String content) {
        String chatRoomId = generateChatRoomId(sender.getId(), receiver.getId());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.sender = sender;
        chatMessage.receiver = receiver;
        chatMessage.content = content;
        chatMessage.chatRoomId = chatRoomId;

        return chatMessage;
    }

    // 채팅방 ID 생성 (작은 ID가 앞에 오도록)
    public static String generateChatRoomId(Long userId1, Long userId2) {
        long smallerId = Math.min(userId1, userId2);
        long largerId = Math.max(userId1, userId2);
        return smallerId + "_" + largerId;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
