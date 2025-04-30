package com.example.aniting.admin.recoResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminRecoResponseViewController {

	@GetMapping("/admin/responses")
	public String adminResponse() {
		return "admin/recommend/responses";
	}
	
	
}
