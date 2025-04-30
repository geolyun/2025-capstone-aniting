package com.example.aniting.admin.recoLog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminRecoLogViewController {

	@GetMapping("/admin/logs")
	public String adminLogs() {
		return "admin/recommend/logs";
	}
	
}
