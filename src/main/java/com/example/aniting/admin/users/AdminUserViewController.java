package com.example.aniting.admin.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminUserViewController {

	@GetMapping("/admin/users")
	public String adminUsers() {
		return "admin/user/users";
	}
	
}
