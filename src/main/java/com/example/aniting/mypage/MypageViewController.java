package com.example.aniting.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageViewController {

	@GetMapping("/user/mypage")
	public String mypage() {
		return "client/mypage/mypage";
	}
	
	@GetMapping("/user/mypage/update")
	public String update() {
		return "client/user/update_user";
	}
	
	@GetMapping("/user/mypage/delete")
	public String delete() {
		return "client/user/delete_user";
	}
	
}
