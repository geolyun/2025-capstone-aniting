package com.example.aniting.admin.recoHistory;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.admin.pet.AdminPetService;
import com.example.aniting.dto.RecommendHistoryDTO;

@RestController
@RequestMapping("/api/admin/history")
public class AdminRecoHistoryController {

	@Autowired
	private AdminRecoHistoryService adminRecoHistoryService;
	
	@Autowired
	private AdminPetService adminPetService;
	
	@GetMapping
    public List<RecommendHistoryDTO> getAllHistory(
            @RequestParam(required = false) String usersId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return adminRecoHistoryService.getFilteredHistory(usersId, from, to);
    }
	
}
