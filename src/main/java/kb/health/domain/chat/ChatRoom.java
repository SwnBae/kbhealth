package kb.health.domain.chat;

import jakarta.persistence.*;
import kb.health.domain.BaseEntity;
import kb.health.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();

    /**
     * 생성
     */
    public static ChatRoom createDirectRoom(Member member1, Member member2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.type = ChatRoomType.DIRECT;

        ChatParticipant participant1 = ChatParticipant.create(chatRoom,member1);
        ChatParticipant participant2 = ChatParticipant.create(chatRoom,member2);

        return chatRoom;
    }

    public static ChatRoom createGroupRoom(String roomName,Member creator, List<Member> members) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomName = roomName;
        chatRoom.type = ChatRoomType.GROUP;

        ChatParticipant creatorParticipant = ChatParticipant.createAsAdmin(chatRoom, creator);

        for (Member member : members) {
            ChatParticipant participant = ChatParticipant.create(chatRoom, member);
        }

        return chatRoom;
    }

}
