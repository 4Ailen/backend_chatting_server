package com.aliens.friendship.backend_chatting_server.chatting.repository;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, Long> {
    @Aggregation(pipeline = {
            "{ '$match': { 'roomId' : ?0 } }",
            "{ '$sort' : { 'chatId' : -1 } }",
            "{ '$limit' : 1 }"
    })
    Optional<Chat> findNewOneChatByRoomId(Long roomId);

    @Aggregation(pipeline = {
            "{ '$match': { 'roomId' : ?0 } }",
            "{ '$sort' : { 'chatId' : -1 } }",
            "{ '$limit' : 100 }"
    })
    Optional<List<Chat>> findHundredChatsByRoomId(Long roomId);


    @Query(value = "{roomId : ?0, read : false, receiverId : ?2}")
    long countUnreadChatsByRoomIdAndReceiverId(Long roomId,Long receiverId );

    @Query(value = "{chatId : ?0")
    Optional<Chat> findByChatId(Long chatId);
}
