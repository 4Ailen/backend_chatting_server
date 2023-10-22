package com.aliens.friendship.backend_chatting_server.db.chat.repository;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSummariesResponse;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<ChatEntity, Long> {
    @Aggregation(pipeline = {
            "{ '$match': { 'roomId' : ?0 } }",
            "{ '$sort' : { 'chatId' : -1 } }",
            "{ '$limit' : 1 }"
    })
    Optional<ChatEntity> findNewOneChatByRoomId(Long roomId);

    @Aggregation(pipeline = {
            "{ '$match': { 'roomId' : ?0 } }",
            "{ '$sort' : { 'chatId' : -1 } }",
            "{ '$limit' : 100 }"
    })
    Optional<List<ChatEntity>> findChatsByRoomId(Long roomId);

    @Aggregation(pipeline = {
            "{ '$match': { 'roomId' : ?0, 'chatId' : { '$lt' : ?1 } } }",
            "{ '$sort' : { 'chatId' : -1 } }",
            "{ '$limit' : 100 }"
    })

    @Query(value = "{roomId : ?0, chatId : {$lt : ?1}, senderId : ?2}")
    Optional<List<ChatEntity>> findNextChatsByRoomId(Long roomId, Long latestChatId, Long memberId);

    @Query(value = "{_id : ?0}")
    Optional<ChatEntity> findByChatId(Long chatId);

    @Query("{'_id': ?0}")
    void updateUnReadCount(Long chatId, int unReadCount);

    @Query(value = "{roomId : ?0, unreadCount : 1}")
    Optional<List<ChatEntity>> findUnreadChatsByRoomId(Long roomId);

    Optional<ChatSummariesResponse> findChatSummariesByRoomId(Long memberId);
}
