package com.example.aniting.admin.recoHistory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminRecoHistoryViewController {

	@GetMapping("/admin/history")
	public String adminHistory() {
		return "admin/recommend/history";
	}
	
}
