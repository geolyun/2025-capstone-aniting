package com.example.aniting.admin.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminUserViewController {

	@GetMapping("/admin/login")
	public String adminLogin() {
		return "admin/user/adminLogin";
	}
	
}
