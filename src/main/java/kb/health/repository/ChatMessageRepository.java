package kb.health.repository;

import kb.health.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지 조회 (페이징)
    Page<ChatMessage> findByChatRoomIdOrderByCreatedDateDesc(String chatRoomId, Pageable pageable);

    // 특정 채팅방의 메시지 조회 (전체)
    List<ChatMessage> findByChatRoomIdOrderByCreatedDate(String chatRoomId);

    // 사용자의 모든 채팅방 목록 조회를 위한 쿼리
    @Query("SELECT DISTINCT c.chatRoomId FROM ChatMessage c WHERE c.sender.id = :userId OR c.receiver.id = :userId")
    List<String> findChatRoomIdsByUserId(@Param("userId") Long userId);

    // 특정 채팅방의 최신 메시지 조회
    ChatMessage findTopByChatRoomIdOrderByCreatedDateDesc(String chatRoomId);

    // 읽지 않은 메시지 개수 조회
    long countByChatRoomIdAndReceiverIdAndIsReadFalse(String chatRoomId, Long receiverId);

    // 메시지 읽음 처리
    @Modifying
    @Query("UPDATE ChatMessage c SET c.isRead = true WHERE c.chatRoomId = :chatRoomId AND c.receiver.id = :receiverId AND c.isRead = false")
    int markMessagesAsRead(@Param("chatRoomId") String chatRoomId, @Param("receiverId") Long receiverId);
}