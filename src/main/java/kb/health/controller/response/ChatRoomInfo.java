package kb.health.controller.response;

import kb.health.domain.Member;
import kb.health.domain.chat.ChatMessage;
import lombok.Getter;

@Getter
public class ChatRoomInfo {
    private final String chatRoomId;
    private final Member partner;
    private final ChatMessage lastMessage;
    private final long unreadCount;

    public ChatRoomInfo(String chatRoomId, Member partner, ChatMessage lastMessage, long unreadCount) {
        this.chatRoomId = chatRoomId;
        this.partner = partner;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }
}
