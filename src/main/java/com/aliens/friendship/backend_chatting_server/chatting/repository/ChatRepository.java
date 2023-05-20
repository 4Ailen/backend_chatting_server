package com.aliens.friendship.backend_chatting_server.chatting.repository;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Chat findByChatId(Long ChatId);
    List<Chat> findAllByRoomId(Long roomId);
    List<Chat> findAllByRoomIdAndReceiverIdAndReadFalse(Long roomId, Long receiverId);
}
