package com.example.aniting.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersMainController {

	@GetMapping("/")
	public String login() {
		return "client/user/login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "client/user/register";
	}
	
	@GetMapping("/find_password")
	public String find_password() {
		return "client/user/find_password";
	}
	
	@GetMapping("/reset_password")
	public String reset_password() {
		return "client/user/reset_password";
	}
	
}
