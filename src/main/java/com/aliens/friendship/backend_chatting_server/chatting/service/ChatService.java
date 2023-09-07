package com.aliens.friendship.backend_chatting_server.chatting.service;

import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSendResponse;
import com.aliens.friendship.backend_chatting_server.chatting.dto.response.ChatSummariesResponse;
import com.aliens.friendship.backend_chatting_server.db.chat.entity.ChatEntity;
import com.aliens.friendship.backend_chatting_server.global.util.jwt.JwtTokenUtil;
import com.aliens.friendship.backend_chatting_server.chatting.converter.ChatConverter;
import com.aliens.friendship.backend_chatting_server.websocket.dto.request.ChatSendRequest;
import com.aliens.friendship.backend_chatting_server.db.chat.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final ChatConverter chatConverter;

    private final MongoTemplate mongoTemplate;

    private final MongoOperations mongoOperations;

    private final JwtTokenUtil jwtTokenUtil;



    public ChatSendResponse saveChat(ChatSendRequest chatSendRequest) {
        ChatEntity singleChatEntity = chatRepository.save(chatConverter.toChatEntityWithRequest(chatSendRequest));
        return chatConverter.toChatSendResponseWithEntity(singleChatEntity);
    }

    public List<ChatSendResponse> getUnreadChatsByRoomId(Long roomId) {
        Optional<List<ChatEntity>> chats = chatRepository.findUnreadChatsByRoomId(roomId);
        List<ChatSendResponse> result = new ArrayList<>();
        for (ChatEntity chatEntity : chats.orElse(new ArrayList<>())) {
            result.add(chatConverter.toChatSendResponseWithEntity(chatEntity));
        }
        return result;
    }

    public Long updateReadStateByRoomId(Long roomId, Long partnerId) {
        Query query = new Query(Criteria.where("roomId").is(roomId).and("senderId").is(partnerId));
        Update update = new Update().set("unreadCount", 0);
        mongoTemplate.updateMulti(query, update, ChatEntity.class);
        return roomId;
    }

    public void updateReadStateByChatId(ChatEntity chatEntity) {
        Query query = new Query(Criteria.where("_id").is(chatEntity.getChatId()));
        Update update = new Update().set("unreadCount", 0);
        mongoTemplate.updateFirst(query, update, ChatEntity.class);
    }

    public ChatSummariesResponse getChatSummaries(List<Long> roomIds, Long memberId) {
        ChatSummariesResponse chatSummariesResponse = new ChatSummariesResponse();
        for(Long roomId: roomIds){
            String lastChatContent = chatRepository.findNewOneChatByRoomId(roomId).isPresent() ? chatRepository.findNewOneChatByRoomId(roomId).get().getChatContent() : "채팅을 시작하세요!";
            String lastChatTime = chatRepository.findNewOneChatByRoomId(roomId).isPresent() ? chatRepository.findNewOneChatByRoomId(roomId).get().getSendTime() : "기록 없음";
            Long numberOfUnreadChats = getNumberOfUnreadChats(roomId,memberId);

            ChatSummariesResponse.ChatSummary chatSummary = ChatSummariesResponse.ChatSummary.builder()
                    .roomId(roomId)
                    .lastChatContent(lastChatContent)
                    .lastChatTime(lastChatTime)
                    .numberOfUnreadChat(numberOfUnreadChats)
                    .build();
            chatSummariesResponse.addChatSummary(chatSummary);
        }
        return chatSummariesResponse;
    }

    public Long getNumberOfUnreadChats(Long roomId, Long memberId) {
        Query query = new Query(Criteria.where("roomId").is(roomId)
                .and("unreadCount").is(1)
                .and("receiverId").is(memberId));
        long count = mongoOperations.count(query, ChatEntity.class);
        return count;
    }

    public ChatEntity getChatEntity(Long chatId) {
        return chatRepository.findByChatId(chatId).get();
    }

    public void validateRoomId(Long roomIdFromPayload, List<Long> roomIds) throws JsonProcessingException {
        if (!roomIds.contains(roomIdFromPayload)) {
            throw new IllegalArgumentException("roomId가 일치하지 않습니다.");
        }
    }

//    public Object getUnreadChatsByRoomId(Long roomId, Long lastPartnerChatId, Long lastMyChatId, Long partnerId, Long memberId) {
//        // 상대의 채팅 아이디중 가장 최근 값 이후의 채팅이 있다면 가져온다.
//        // 내 채팅 아이디중 가장 최근 값 이후의 채팅이 있다면 가져온다.
//        // 각각의 배열로 넣어 반환한다.
//        List<ChatEntity> partnerChats = chatRepository.findNextChatsByRoomId(roomId, lastPartnerChatId,partnerId).orElse(new ArrayList<>());
//        List<ChatEntity> myChats = chatRepository.findNextChatsByRoomId(roomId, lastMyChatId,memberId).orElse(new ArrayList<>());
//        List<ChatSendResponse> partnerChatSendResponses = new ArrayList<>();
//        List<ChatSendResponse> myChatSendResponses = new ArrayList<>();
//        for (ChatEntity chatEntity : partnerChats) {
//            partnerChatSendResponses.add(chatConverter.toChatSendResponseWithEntity(chatEntity));
//        }
//        for (ChatEntity chatEntity : myChats) {
//            myChatSendResponses.add(chatConverter.toChatSendResponseWithEntity(chatEntity));
//        }
//        return new Object[]{partnerChatSendResponses, myChatSendResponses};
//    }
}