package com.example.aniting.admin.recoLog;

import java.time.LocalDateTime;
import java.util.List;

import com.example.aniting.dto.RecommendLogDTO;

public interface AdminRecoLogService {

	public List<RecommendLogDTO> getFilteredLogs(String usersId, String keyword, LocalDateTime from, LocalDateTime to);

}
