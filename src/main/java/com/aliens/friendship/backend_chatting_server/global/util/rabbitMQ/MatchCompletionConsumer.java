package com.aliens.friendship.backend_chatting_server.global.util.rabbitMQ;

import com.aliens.friendship.backend_chatting_server.fcm.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MatchCompletionConsumer {

    private final FcmService fcmService;
    @RabbitListener(queues = "match.completion.queue")
    public void receiveMatchCompletionMessage(String message) throws FirebaseMessagingException {
        fcmService.sendNoticeToAll("FriendShip", message);
        log.info("MatchCompletionConsumer: {}", message);
    }
}
