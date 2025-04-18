package com.example.aniting.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageMainController {

	@GetMapping("/mypage")
	public String mypage() {
		return "client/mypage/mypage";
	}
	
	@GetMapping("/mypage/update")
	public String update() {
		return "client/user/update_user";
	}
	
	@GetMapping("/mypage/delete")
	public String delete() {
		return "client/user/delete_user";
	}
	
}
