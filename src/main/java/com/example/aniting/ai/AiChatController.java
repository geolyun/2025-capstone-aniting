package com.example.aniting.ai;

import com.example.aniting.dto.UsersDTO;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommend")
public class AiChatController {
	
	@Value("${kakaoMap.script.api.key}")
	private String kakaoMapApiKey;

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

    @GetMapping("/map")
    public String mapPage(HttpServletRequest request, Model model) {
        UsersDTO user = (UsersDTO) request.getSession(false).getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        
        model.addAttribute("kakaoMapKey", kakaoMapApiKey);
        return "ai/map/map";  // templates/ai/map.html
    }
}
