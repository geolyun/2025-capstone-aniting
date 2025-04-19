package com.example.aniting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // ✅ View를 렌더링할 때는 반드시 이걸 써야 함
public class AiChatViewController {

    @GetMapping("/recommend/chat")
    public String showChatPage() {
        return "ai/chat/chat"; // templates/ai/chat.html 렌더링됨
    }
}