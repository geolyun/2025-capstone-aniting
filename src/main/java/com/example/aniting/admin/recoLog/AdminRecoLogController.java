package com.example.aniting.admin.recoLog;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.dto.RecommendLogDTO;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminRecoLogController {

	@Autowired
	private AdminRecoLogService adminRecoLogService;
	
	@GetMapping
    public List<RecommendLogDTO> getLogs(
            @RequestParam(required = false) String usersId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return adminRecoLogService.getFilteredLogs(usersId, keyword, from, to);
    }
	
}
