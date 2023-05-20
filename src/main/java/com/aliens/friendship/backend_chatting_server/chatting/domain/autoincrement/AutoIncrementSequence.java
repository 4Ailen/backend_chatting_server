package com.aliens.friendship.backend_chatting_server.chatting.domain.autoincrement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "auto_sequence")
public class AutoIncrementSequence {

    @Id
    private String id;
    private Long seq;
}