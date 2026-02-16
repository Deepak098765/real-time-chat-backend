package com.deepak.chat.chat_app_backend.controllers;

import com.deepak.chat.chat_app_backend.ai.service.AsyncSmartReplyService;
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
import com.deepak.chat.chat_app_backend.config.AppConstants;
import com.deepak.chat.chat_app_backend.config.WebConfig;

import java.time.Instant;
import java.time.LocalDateTime;

@Controller

public class ChatController {

    private RoomRepository roomRepository;
    private final AsyncSmartReplyService asyncSmartReplyService;


//    public ChatController(RoomRepository roomRepository) {
//        this.roomRepository = roomRepository;
//    }

    public ChatController(RoomRepository roomRepository,
                          AsyncSmartReplyService asyncSmartReplyService) {
        this.roomRepository = roomRepository;
        this.asyncSmartReplyService = asyncSmartReplyService;
    }


    // for sending and receiving messages

    @MessageMapping("/sendMessage/{roomId}")// /app/chat/sendMessage/roomId : here message will be send
    @SendTo("/topic/room/{roomId}") // subscribe: this endpoint is subscribed by other client
    public Message sendMessage(
            @DestinationVariable String roomId,
            @RequestBody MessageRequest request
    ){
        //Before changing this line app was working
        //Room room = roomRepository.findByRoomId(request.getRoomId());

        Room room = roomRepository.findByRoomId(roomId);

        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(request.getSender());
        message.setTimeStamp(Instant.now());

//        if(room != null){
//            room.getMessage().add(message);
//            roomRepository.save(room);
//        }else{
//            throw new RuntimeException("Room not found !!!");
//        }
//
//        return message;

        if(room != null){
            room.getMessage().add(message);
            roomRepository.save(room);

            // ✅ ADD HERE — AFTER SAVE
            asyncSmartReplyService.generateSmartReplyAsync(
                    message.getContent(),
                    room.getRoomId()
            );

        }else{
            throw new RuntimeException("Room not found !!!");
        }

        return message;

    }
}
