package com.example.aniting.admin.pet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPetViewController {

	@GetMapping("/admin/pets")
	public String adminPet() {
		return "admin/pet/pets";
	}
	
}
