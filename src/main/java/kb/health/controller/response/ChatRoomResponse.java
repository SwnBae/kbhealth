package kb.health.controller.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponse {
    private final String chatRoomId;
    private final Long partnerId;
    private final String partnerName;
    private final String partnerProfileImage;
    private final String lastMessage;
    private final LocalDateTime lastMessageTime;
    private final Long lastMessageSenderId;
    private final String lastMessageSenderName;
    private final long unreadCount;

    public ChatRoomResponse(String chatRoomId, Long partnerId, String partnerName, String partnerProfileImage,
                            String lastMessage, LocalDateTime lastMessageTime, Long lastMessageSenderId,
                            String lastMessageSenderName, long unreadCount) {
        this.chatRoomId = chatRoomId;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerProfileImage = partnerProfileImage;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageSenderName = lastMessageSenderName;
        this.unreadCount = unreadCount;
    }

    public static ChatRoomResponse create(ChatRoomInfo chatRoomInfo) {
        return new ChatRoomResponse(
                chatRoomInfo.getChatRoomId(),
                chatRoomInfo.getPartner().getId(),
                chatRoomInfo.getPartner().getUserName(),
                chatRoomInfo.getPartner().getProfileImageUrl(),
                chatRoomInfo.getLastMessage().getContent(),
                chatRoomInfo.getLastMessage().getCreatedDate(),
                chatRoomInfo.getLastMessage().getSender().getId(),
                chatRoomInfo.getLastMessage().getSender().getUserName(),
                chatRoomInfo.getUnreadCount()
        );
    }
}
