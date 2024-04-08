package gg.garen.back.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gg.garen.back.chatting.dto.ChatDto;
import gg.garen.back.chatting.entity.Chat;
import gg.garen.back.chatting.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, ChatDto> redisTemplateGet;
    private final RedisTemplate<String, ChatDto> redisTemplateSave;

    public void saveChat(ChatDto chatDto) {
        try {
            String keyForGet = "chat:messagesGet:" + chatDto.getRoomId();
            String keyForSave = "chat:messagesSave:" + chatDto.getRoomId();
            redisTemplateGet.opsForList().leftPush(keyForGet, chatDto);
            redisTemplateSave.opsForList().rightPush(keyForSave, chatDto);

            // 리스트의 크기를 최신 100개로 유지
            redisTemplateGet.opsForList().trim(keyForGet, 0, 99);

        }catch (Exception e) {
            log.error("Exception {}", e.getMessage());

        }
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void saveChatsToMongoDB() {

        String keyForSave = "chat:messagesSave:" + 1;
        List<ChatDto> chatDtoList = redisTemplateSave.opsForList().range(keyForSave, 0, -1);
        List<Chat> chatList = new ArrayList<>();
        for(ChatDto chatDto : chatDtoList) {
            chatList.add(new Chat(chatDto.getRoomId(),
                    chatDto.getUserId(), chatDto.getContent(), chatDto.getTime()));
        }

        chatRepository.saveAll(chatList);
        redisTemplateSave.delete(keyForSave);

    }

    public List<ChatDto> getChat() {
        try {
            String keyForGet = "chat:messagesGet:" + 1;
            List<ChatDto> messages = redisTemplateGet.opsForList().range(keyForGet, 0, 99);
            return messages;
        } catch (Exception e) {
            log.error("Error retrieving chats: {}", e.getMessage());
            return List.of(); // 예외가 발생했을 때 빈 리스트 반환
        }
    }

}
