package com.example.aniting.admin.category;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminCategoryVeiwController {

	@GetMapping("/admin/categories")
	public String adminCategories() {
		return "admin/category/categories";
	}
	
}
