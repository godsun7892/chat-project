package gg.garen.back.chatting.service;

import gg.garen.back.chatting.dto.ChatDto;
import gg.garen.back.chatting.entity.Chat;
import gg.garen.back.chatting.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    public final ChatRepository chatRepository;

    public Chat saveChat(ChatDto dto) {
        try {
            Chat chat = Chat.builder()
                    .roomId(dto.getRoomId())
                    .userId(dto.getUserId())
                    .content(dto.getContent())
                    .build();
            return chatRepository.save(chat);
        }catch (Exception e) {
            log.error("Exception {}", e.getMessage());
            return null;
        }

    }

    public List<ChatDto> getChat() {
        try {
            PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "_id"));
            List<Chat> chats = chatRepository.findAll(pageRequest).getContent();
            List<ChatDto> list = new ArrayList<>();
            for (Chat chat : chats) {
                list.add(ChatDto.of(chat.getContent(), chat.getUserId(), chat.getRoomId(), chat.getCreatedAt()));
            }
            return list;
        } catch (Exception e) {
            log.error("Error retrieving chats: {}", e.getMessage());
            return List.of(); // 예외가 발생했을 때 빈 리스트 반환
        }
    }

}
