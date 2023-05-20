package com.aliens.friendship.backend_chatting_server.chatting.domain.autoincrement;

import com.aliens.friendship.backend_chatting_server.chatting.domain.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatListener extends AbstractMongoEventListener<Chat> {

    private final SequenceGeneratorService generatorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Chat> event) {
        event.getSource().setId(generatorService.generateSequence(Chat.SEQUENCE_NAME));
    }
}