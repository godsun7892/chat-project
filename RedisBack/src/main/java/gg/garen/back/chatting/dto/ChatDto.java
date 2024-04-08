package gg.garen.back.chatting.dto;

import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@ToString
@Getter
public class ChatDto {

    private String content;

    // 3) 보낸 이
    private String userId;

    // 4) 해당 메세지가 오고간 채팅방 번호
    private int roomId;

    private String time = "";

    private ChatDto(String content, String userId, int roomId){
        this.content = content;
        this.userId = userId;
        this.roomId = roomId;
    }

    public void setTime() {
        this.time = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}