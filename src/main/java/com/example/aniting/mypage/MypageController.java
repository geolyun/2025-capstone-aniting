package com.example.aniting.mypage;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.aniting.config.SecurityConfig;
import com.example.aniting.dto.RecommendHistoryDTO;
import com.example.aniting.dto.UsersDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/mypage")
public class MypageController {

    private final SecurityConfig securityConfig;

	@Autowired
	private MypageService mypageService;

    MypageController(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }
	
	@GetMapping("/info")
	public UsersDTO getUserInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UsersDTO user = (UsersDTO) session.getAttribute("user");
		return mypageService.getUserInfo(user.getUsersId());
	}
	
	@GetMapping("/history")
	public List<Map<String, Object>> getRecommendHistory(HttpServletRequest request) {
		HttpSession session = request.getSession();
	    UsersDTO user = (UsersDTO) session.getAttribute("user");
	    return mypageService.getRecommendHistory(user.getUsersId());
	}
	
}
