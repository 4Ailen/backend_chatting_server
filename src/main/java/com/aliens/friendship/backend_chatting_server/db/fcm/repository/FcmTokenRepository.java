package com.aliens.friendship.backend_chatting_server.db.fcm.repository;

import com.aliens.friendship.backend_chatting_server.db.fcm.entity.FcmTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmTokenRepository extends MongoRepository<FcmTokenEntity, Long> {
    List<FcmTokenEntity> findAllByMemberId(Long memberId);
}
