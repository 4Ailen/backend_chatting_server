package com.aliens.friendship.backend_chatting_server.db.chat.entity.autoincrement;

import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatListener extends AbstractMongoEventListener<ChatEntity> {

    private final SequenceGeneratorService generatorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<ChatEntity> event) {
        event.getSource().setId(generatorService.generateSequence(ChatEntity.SEQUENCE_NAME));
    }
}