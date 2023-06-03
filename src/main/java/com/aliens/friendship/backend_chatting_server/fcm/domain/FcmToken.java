package com.aliens.friendship.backend_chatting_server.fcm.domain;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Document(collection = "fcm_token")
public class FcmToken {

    @Id
    private Long memberId;

    private String token;

}

