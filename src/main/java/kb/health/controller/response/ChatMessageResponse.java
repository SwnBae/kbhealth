package kb.health.controller.response;

import kb.health.domain.chat.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {
    private final Long id;
    private final Long senderId;
    private final String senderName;
    private final String senderProfileImage;
    private final Long receiverId;
    private final String receiverName;
    private final String content;
    private final boolean isRead;
    private final LocalDateTime createdDate;
    private final String chatRoomId;

    public ChatMessageResponse(Long id, Long senderId, String senderName, String senderProfileImage,
                               Long receiverId, String receiverName, String content, boolean isRead,
                               LocalDateTime createdDate, String chatRoomId) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfileImage = senderProfileImage;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.content = content;
        this.isRead = isRead;
        this.createdDate = createdDate;
        this.chatRoomId = chatRoomId;
    }

    public static ChatMessageResponse create(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getSender().getId(),
                chatMessage.getSender().getUserName(),
                chatMessage.getSender().getProfileImageUrl(),
                chatMessage.getReceiver().getId(),
                chatMessage.getReceiver().getUserName(),
                chatMessage.getContent(),
                chatMessage.isRead(),
                chatMessage.getCreatedDate(),
                chatMessage.getChatRoomId()
        );
    }
}
