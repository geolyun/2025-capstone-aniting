package com.example.aniting.admin.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardViewController {

	@GetMapping("/admin/main")
	public String adminDashboard() {
		return "admin/dashboard/dashboard";
	}
	
}
