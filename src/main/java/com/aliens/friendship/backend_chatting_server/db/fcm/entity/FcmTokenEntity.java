package com.aliens.friendship.backend_chatting_server.db.fcm.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "fcm_token")
public class FcmTokenEntity {
    @Id
    private String id;
    private Long memberId;
    private String value;

    public FcmTokenEntity(
            String memberPersonalId,
            String value
    ) {
        this.memberId = Long.parseLong(memberPersonalId);
        this.value = value;
    }
}