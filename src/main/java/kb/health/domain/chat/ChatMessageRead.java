package kb.health.domain.chat;

import jakarta.persistence.*;
import kb.health.domain.BaseEntity;
import kb.health.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRead extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chat_message_read_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ChatMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id")
    private Member reader;

    /**
     * 생성
     */
    public static ChatMessageRead create(ChatMessage message, Member reader) {
        ChatMessageRead chatMessageRead = new ChatMessageRead();
        chatMessageRead.message = message;
        chatMessageRead.reader = reader;

        return chatMessageRead;
    }

    // 읽음 날짜는 생성일자
    public LocalDateTime getReadAt() {
        return getCreatedDate();
    }
}
