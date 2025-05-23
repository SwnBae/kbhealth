package kb.health.domain.chat;

import jakarta.persistence.*;
import kb.health.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    private String roomName;

    @Enumerated(EnumType.STRING)
    private ChatRoomType type;

    private boolean isActive = true;


}
