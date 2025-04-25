package com.example.aniting.controller;

import com.example.aniting.dto.UsersDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommend")
public class AiChatViewController {

    @GetMapping("/chat")
    public String chatPage(HttpServletRequest request) {
        UsersDTO user = (UsersDTO) request.getSession(false).getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        return "ai/chat/chat";
    }

    @GetMapping("/result")
    public String resultPage(HttpServletRequest request) {
        UsersDTO user = (UsersDTO) request.getSession(false).getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        return "ai/result/result";
    }
}
