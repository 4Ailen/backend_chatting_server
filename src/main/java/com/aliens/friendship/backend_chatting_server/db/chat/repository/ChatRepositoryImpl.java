package com.aliens.friendship.backend_chatting_server.db.chat.repository;

import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ChatRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ChatRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setUnReadCountToZero(String chatId) {
        ChatEntity chatEntity = mongoTemplate.findOne(query(where("chatId").is(chatId)), ChatEntity.class);
        if (chatEntity != null) {
            chatEntity.changeReadState();
            mongoTemplate.save(chatEntity);
        }
    }
}
