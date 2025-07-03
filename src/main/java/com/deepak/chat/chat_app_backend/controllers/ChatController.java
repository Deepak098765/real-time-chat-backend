package com.deepak.chat.chat_app_backend.controllers;

import com.deepak.chat.chat_app_backend.entities.Message;
import com.deepak.chat.chat_app_backend.entities.Room;
import com.deepak.chat.chat_app_backend.payload.MessageRequest;
import com.deepak.chat.chat_app_backend.repositories.RoomRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {

    private RoomRepository roomRepository;

    public ChatController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // for sending and receiving messages

    @MessageMapping("/sendMessage/{roomId}")// /app/chat/sendMessage/roomId : here message will be send
    @SendTo("/topic/room/{roomId}") // subscribe: this endpoint is subscribed by other client
    public Message sendMessage(
            @DestinationVariable String roomId,
            @RequestBody MessageRequest request
    ){
        Room room = roomRepository.findByRoomId(request.getRoomId());

        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(request.getSender());
        message.setTimeStamp(LocalDateTime.now());

        if(room != null){
            room.getMessage().add(message);
            roomRepository.save(room);
        }else{
            throw new RuntimeException("Room not found !!!");
        }

        return message;
    }
}
