package com.aliens.friendship.backend_chatting_server.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberIdWithFCMTokenDto {
    private Long memberId;
    private String fcmToken;
}
