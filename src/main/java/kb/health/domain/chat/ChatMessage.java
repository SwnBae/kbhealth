package kb.health.domain.chat;

import jakarta.persistence.*;
import kb.health.domain.BaseEntity;
import kb.health.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private ChatMessageType messageType = ChatMessageType.TEXT;

    private String fileUrl;

    private String fileName;

    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<ChatMessageRead> readStatuses = new ArrayList<>();

    /**
     * 메시지 생성
     */
    public static ChatMessage createTextMessage(ChatRoom chatRoom, Member sender, String content) {
        ChatMessage message = new ChatMessage();
        message.chatRoom = chatRoom;
        message.sender = sender;
        message.content = content;
        message.messageType = ChatMessageType.TEXT;

        return message;
    }

    public static ChatMessage createFileMessage(ChatRoom chatRoom, Member sender,
                                                String fileName, String fileUrl) {
        ChatMessage message = new ChatMessage();
        message.chatRoom = chatRoom;
        message.sender = sender;
        message.content = fileName;
        message.messageType = ChatMessageType.FILE;
        message.fileName = fileName;
        message.fileUrl = fileUrl;

        return message;
    }

    public static ChatMessage createImageMessage(ChatRoom chatRoom, Member sender, String fileName,String fileUrl) {
        ChatMessage message = createFileMessage(chatRoom, sender, fileName, fileUrl);
        message.messageType = ChatMessageType.IMAGE;

        return message;
    }

    // 시스템 메시지 (입장, 퇴장 등)
    public static ChatMessage createSystemMessage(ChatRoom chatRoom, String content) {
        ChatMessage message = new ChatMessage();
        message.chatRoom = chatRoom;
        message.content = content;
        message.messageType = ChatMessageType.SYSTEM;
        return message;
    }

    /**
     * 비즈니스 로직
     */
    // 읽음 처리 확인
    public boolean isReadBy(Long memberId) {
        return readStatuses.stream()
                .anyMatch(r -> r.getReader().getId().equals(memberId));
    }

    // 삭제 처리
    public void markAsDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 읽음 처리, 연관관계 메서드
     */
    public void addReadStatus(Member reader){
        if(!isReadBy(reader.getId())) {
            ChatMessageRead read = ChatMessageRead.create(this,reader);
            this.readStatuses.add(read);
        }
    }
}
