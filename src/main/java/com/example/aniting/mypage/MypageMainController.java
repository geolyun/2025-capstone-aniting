package com.example.aniting.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageMainController {

	@GetMapping("/mypage")
	public String mypage() {
		return "client/mypage/mypage";
	}
	
}
