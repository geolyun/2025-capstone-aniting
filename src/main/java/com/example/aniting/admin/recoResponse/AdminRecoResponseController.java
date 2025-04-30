package com.example.aniting.admin.recoResponse;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.dto.RecommendResponseDTO;

@RestController
@RequestMapping("/api/admin/responses")
public class AdminRecoResponseController {

	@Autowired
	private AdminRecoResponseService adminRecoResponseService;
	
	@GetMapping
    public List<RecommendResponseDTO> getResponses(
            @RequestParam(required = false) String usersId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return adminRecoResponseService.getFilteredResponses(usersId, keyword, from, to);
    }
	
	@DeleteMapping("/{id}")
    public void deleteResponse(@PathVariable Long id) {
        adminRecoResponseService.deleteById(id);
    }
	
}
