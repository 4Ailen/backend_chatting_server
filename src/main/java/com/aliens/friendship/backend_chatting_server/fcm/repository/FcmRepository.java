package com.aliens.friendship.backend_chatting_server.fcm.repository;

import com.aliens.friendship.backend_chatting_server.fcm.domain.FcmToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmRepository extends MongoRepository<FcmToken, Long> {
    
}
