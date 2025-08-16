package com.example.demo.controller;

import com.example.demo.config.OnlineUserTracker;
import com.example.demo.helper.ViewHelper;
import com.example.demo.model.ChatHistory;
import com.example.demo.model.ChatMessage;
import com.example.demo.repository.ChatHistoryRepository;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatHistoryRepository chatRepo;
    private final OnlineUserTracker userTracker;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatHistoryRepository chatRepo, OnlineUserTracker userTracker) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepo = chatRepo;
        this.userTracker = userTracker;
    }

    @MessageMapping("/chat/{room}")
    public void sendMessage(@DestinationVariable String room, ChatHistory message) {
        message.setTimestamp(LocalDateTime.now());
        chatRepo.save(message);
        messagingTemplate.convertAndSend("/topic/" + room, message);

        // Gửi danh sách người dùng online
        messagingTemplate.convertAndSend("/topic/" + room + "/users", userTracker.getUsers(room));
    }

    @MessageMapping("/chat/{room}/join")
    public void joinRoom(@DestinationVariable String room, ChatHistory message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId(); // ✅ lấy sessionId
        userTracker.userJoined(sessionId, room, message.getSender());
        // Gửi danh sách người dùng online
        messagingTemplate.convertAndSend("/topic/" + room + "/users", userTracker.getUsers(room));
    }

    @MessageMapping("/chat/{room}/typing")
    public void typing(@DestinationVariable String room, ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/" + room + "/typing", message.getSender());
    }

    // Khi load chat.html, gửi lịch sử tin nhắn
    @GetMapping("/chat")
    public String chatPage(@RequestParam(required = false) String room, Model model) {
        if (room != null && !room.isEmpty()) {
            try {
                List<ChatHistory> history = chatRepo.findByRoomOrderByTimestampAsc(room);
                model.addAttribute("history", history);
            }catch (Exception e) {
                model.addAttribute("history", null);
            }
            model.addAttribute("room", room);
        } else {
            model.addAttribute("room", ""); // để client tự lấy từ localStorage
        }
        ViewHelper.setView(model, "view/chat", "Chat Room");
        return "layout"; // Load layout.html
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        OnlineUserTracker.RoomNickname info = userTracker.getUserInfo(sessionId);
        if (info != null) {
            userTracker.userLeft(sessionId);
            messagingTemplate.convertAndSend("/topic/" + info.getRoom() + "/users", userTracker.getUsers(info.getRoom()));
        }
    }

    @PostMapping("/react/{room}/{messageId}")
    @ResponseBody
    public ResponseEntity<?> reactToMessage(@PathVariable String room,
                                            @PathVariable Long messageId,
                                            @RequestParam String emoji) {
        List<ChatHistory> history = chatRepo.findByRoomOrderByTimestampAsc(room);
        if (history == null) return ResponseEntity.notFound().build();

        for (ChatHistory msg : history) {
            if (msg.getId().equals(messageId)) {
                var reaction = msg.getReactions();
                reaction.merge(emoji, 1, Integer::sum);
                msg.setReactions(reaction);

                // ✅ Lưu vào DB
                chatRepo.save(msg);

                // Broadcast lại để cập nhật
                messagingTemplate.convertAndSend("/topic/" + room, msg);
                return ResponseEntity.ok(msg);
            }
        }

        return ResponseEntity.notFound().build();
    }

}
