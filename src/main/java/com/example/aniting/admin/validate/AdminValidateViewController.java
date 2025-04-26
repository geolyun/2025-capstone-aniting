package com.example.aniting.admin.validate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminValidateViewController {

	@GetMapping("/admin/login")
	public String adminLogin() {
		return "admin/user/adminLogin";
	}
	
}
