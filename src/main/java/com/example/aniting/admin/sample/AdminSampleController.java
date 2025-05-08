package com.example.aniting.admin.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/sample")
public class AdminSampleController {

	@Autowired
	private AdminSampleService adminSampleService;
	
	@PostMapping("/generate")
    public String generateSample(@RequestParam(defaultValue = "1") int count) {
        return adminSampleService.generateMultipleSamples(count);
    }
	
}
