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
public class ChatParticipant extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chat_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ChatParticipateRole role = ChatParticipateRole.MEMBER;

    private LocalDateTime lastReadAt;

    private boolean isActive = true;

    /**
     * 생성
     */
    public static ChatParticipant create(ChatRoom chatRoom, Member member) {
        ChatParticipant participant = new ChatParticipant();

        participant.role = ChatParticipateRole.MEMBER;
        participant.lastReadAt = LocalDateTime.now();
        participant.addChatRoom(chatRoom);
        participant.addMember(member);

        return participant;
    }

    public static ChatParticipant createAsAdmin(ChatRoom chatRoom, Member member) {
        ChatParticipant participant = create(chatRoom, member);
        participant.role = ChatParticipateRole.ADMIN;
        return participant;
    }

    /**
     * 연관관계 메서드
     */
    public void addChatRoom(ChatRoom chatRoom){
        chatRoom.getParticipants().add(this);
        this.chatRoom = chatRoom;
    }

    //양방향 사용 안할것
    public void addMember(Member member){
        this.member = member;
    }

    public void removeChatRoom(ChatRoom chatRoom){
        chatRoom.getParticipants().remove(this);
        this.chatRoom = null;
    }

    public void removeMember(Member member){
        this.member = null;
    }

}
