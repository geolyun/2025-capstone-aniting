package com.example.aniting.admin.sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminSampleViewController {

	@GetMapping("/admin/sample")
	public String adminResponse() {
		return "admin/sample/sample";
	}
	
}
